package com.zju.lab.bs.hepatitis.handlers;

import com.zju.lab.bs.hepatitis.utils.AppUtil;
import com.zju.lab.bs.hepatitis.utils.SQLUtil;
import com.zju.lab.bs.hepatitis.annotations.RouteHandler;
import com.zju.lab.bs.hepatitis.annotations.RouteMapping;
import com.zju.lab.bs.hepatitis.annotations.RouteMethod;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhaitao on 2015/12/10.
 */
@RouteHandler("/api/users")
public class UserHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

    @RouteMapping(method = RouteMethod.GET)
    public Handler<RoutingContext> list() {
        return ctx -> {
            LOGGER.debug("Start get list");
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());

            client.getConnection(conn -> {
                LOGGER.debug("Succeed {}", conn.succeeded());
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "select USERNAME, FIRST_NAME, LAST_NAME, ADDRESS from USER", rs -> {
                    JsonArray users = new JsonArray();
                    for (JsonObject result : rs.getRows()) {
                        if ("admin".equals(result.getString("USERNAME"))){
                            result.put("ROLE", "admin");
                        }
                        else{
                            result.put("ROLE", "user");
                        }
                        users.add(result);
                    }
                    ctx.response().end(users.encode());
                    SQLUtil.close(conn.result());
                });
            });
        };
    }

    @RouteMapping(method = RouteMethod.POST)
    public Handler<RoutingContext> add() {
        return ctx -> {
            JsonObject user = ctx.getBodyAsJson();
            String username = user.getString("username");
            String password = user.getString("password");
            String firstName = user.getString("first_name");
            String lastName = user.getString("last_name");
            String address = user.getString("address");

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                LOGGER.error("Username and Password cannot be null");
                JsonObject error = new JsonObject();
                error.put("error", "Username and Password cannot be null");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            String salt = AppUtil.computeHash(username, null, "SHA-512");
            String passwordHash = AppUtil.computeHash(password, salt, "SHA-512");

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());

            client.getConnection(conn -> {

                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "select * from USER where USERNAME = ?", new JsonArray().add(username), rs -> {
                    if (rs.getResults().size() >= 1) {
                        SQLUtil.close(conn.result());
                        LOGGER.error("User with username {} already exists..", username);
                        JsonObject error = new JsonObject();
                        error.put("error", "User with username " + username+ " already exists");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }

                    JsonArray params = new JsonArray();
                    params.add(username).add(passwordHash).add(salt).add(firstName).add(lastName).add(address);
                    SQLUtil.update(conn.result(), "insert into USER (USERNAME, PASSWORD, PASSWORD_SALT, FIRST_NAME, LAST_NAME, ADDRESS) values (?, ?, ?, ?, ?, ?)", params, insert -> {
                        SQLUtil.update(conn.result(), "insert into USER_ROLES(USERNAME,ROLE) values(?,?)", new JsonArray().add(username).add("user"), done -> {
                            SQLUtil.query(conn.result(), "select USERNAME, FIRST_NAME, LAST_NAME, ADDRESS from USER where USERNAME = ?", new JsonArray().add(username), rs2 -> {
                                SQLUtil.close(conn.result());
                                ctx.response().end(rs2.getRows().get(0).encode());
                            });
                        });
                    });
                });
            });
        };
    }

    @RouteMapping(value = "/:username", method = RouteMethod.GET)
    public Handler<RoutingContext> edit() {
        return ctx -> {
            String username = ctx.request().getParam("USERNAME");
            if (StringUtils.isBlank(username)) {
                LOGGER.error("Username is blank");
                ctx.fail(404);
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {

                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "select USERNAME, FIRST_NAME, LAST_NAME, ADDRESS from USER where USERNAME = ?", new JsonArray().add(username), res -> {
                    SQLUtil.close(conn.result());
                    if (res.getRows().size() == 1) {
                        ctx.response().end(res.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/:username", method = RouteMethod.PUT)
    public Handler<RoutingContext> update() {
        return ctx -> {
            JsonObject user = ctx.getBodyAsJson();
            String username = user.getString("USERNAME");
            if (StringUtils.isBlank(username)) {
                LOGGER.error("Username is blank");
                ctx.fail(404);
            }

            String firstName = user.getString("FIRST_NAME");
            String lastName = user.getString("LAST_NAME");
            String address = user.getString("ADDRESS");

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(404);
                }

                JsonArray params = new JsonArray();
                params.add(firstName).add(lastName).add(address).add(username);
                SQLUtil.update(conn.result(), "update USER set FIRST_NAME = ?, LAST_NAME = ?, ADDRESS = ? where USERNAME = ?", params, res -> {
                    SQLUtil.query(conn.result(), "select USERNAME, FIRST_NAME, LAST_NAME, ADDRESS from USER where USERNAME = ?", new JsonArray().add(username), rs -> {
                        SQLUtil.close(conn.result());
                        ctx.response().end(rs.getRows().get(0).encode());
                    });
                });
            });
        };
    }

    @RouteMapping(value = "/:username", method = RouteMethod.DELETE)
    public Handler<RoutingContext> delete() {
        return ctx -> {
            String username = ctx.request().getParam("username");
            if (StringUtils.isBlank(username)) {
                LOGGER.error("Username is blank");
                ctx.fail(404);
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(404);
                }

                SQLUtil.update(conn.result(), "delete from USER where USERNAME = ?", new JsonArray().add(username), res -> {
                    SQLUtil.update(conn.result(), "delete from USER_ROLES where USERNAME = ?", new JsonArray().add(username), res2 -> {
                        SQLUtil.close(conn.result());
                        ctx.response().end();
                    });
                });

            });
        };
    }

}
