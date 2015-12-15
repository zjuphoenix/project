package com.zju.lab.bs.hepatitis.handlers;

import com.zju.lab.bs.hepatitis.annotations.RouteHandler;
import com.zju.lab.bs.hepatitis.annotations.RouteMapping;
import com.zju.lab.bs.hepatitis.annotations.RouteMethod;
import com.zju.lab.bs.hepatitis.utils.AppUtil;
import com.zju.lab.bs.hepatitis.utils.SQLUtil;
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
 * Created by wuhaitao on 2015/12/13.
 */
@RouteHandler("/examinations")
public class HepatitisHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HepatitisHandler.class);

    @RouteMapping(method = RouteMethod.GET)
    public Handler<RoutingContext> getExaminationList() {
        return ctx -> {
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT * FROM examination", rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() >= 1) {
                        JsonArray examinations = new JsonArray();
                        for (JsonObject row : rs.getRows()) {
                            examinations.add(row);
                        }
                        ctx.response().end(examinations.encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/detail/:id", method = RouteMethod.GET)
    public Handler<RoutingContext> getExaminationById() {
        return ctx -> {
            int id = Integer.parseInt(ctx.request().getParam("id"));
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }
                SQLUtil.query(conn.result(), "SELECT * FROM examination WHERE id = ?", new JsonArray().add(id), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/count", method = RouteMethod.GET)
    public Handler<RoutingContext> getExaminationCount() {
        return ctx -> {
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT COUNT(*) FROM examination", rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() >= 1) {
                        ctx.response().end(rs.getRows().get(0).getInteger("COUNT(*)").toString());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    /*@RouteMapping(value = "/:page", method = RouteMethod.GET)
    public Handler<RoutingContext> getExaminationListByPage() {
        return ctx -> {
            int page = Integer.parseInt(ctx.request().getParam("page"));
            String hepatitisType = ctx.request().getParam("hepatitisType");
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }
                SQLUtil.query(conn.result(), "SELECT patientId,gender,age,occupation,city,hepatitisBFamilyHistory,liverCancerFamilyHistory,contactHistory,alcoholismHistory,hepatitisBPastHistory FROM examination WHERE hepatitisType = ? LIMIT ?,20", new JsonArray().add(hepatitisType).add(page * 20), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }*/

    @RouteMapping(value = "/info/:hepatitisType/:page", method = RouteMethod.GET)
    public Handler<RoutingContext> getInfoByType() {
        return ctx -> {
            String hepatitisType = ctx.request().getParam("hepatitisType");
            int page = Integer.parseInt(ctx.request().getParam("page"));
            if (StringUtils.isBlank(hepatitisType)) {
                LOGGER.error("hepatitisType is blank");
                JsonObject error = new JsonObject();
                error.put("error", "hepatitisType should not be blank");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT patientId,gender,age,occupation,city,hepatitisBFamilyHistory,liverCancerFamilyHistory,contactHistory,alcoholismHistory,hepatitisBPastHistory FROM examination WHERE hepatitisType = ? LIMIT ?,20", new JsonArray().add(hepatitisType).add(page*20), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/TCM/:hepatitisType/:page", method = RouteMethod.GET)
    public Handler<RoutingContext> getTCMByType() {
        return ctx -> {
            String hepatitisType = ctx.request().getParam("hepatitisType");
            int page = Integer.parseInt(ctx.request().getParam("page"));
            if (StringUtils.isBlank(hepatitisType)) {
                LOGGER.error("hepatitisType is blank");
                JsonObject error = new JsonObject();
                error.put("error", "hepatitisType should not be blank");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT patientId,weakness,inappetence,yelloweye,yellowurine,abdominalpain,abdominaldistension,hematemesisandmelena,pruritus,hepaticface,liverpalms,skinsclerajaundice,gallbladderMurphysign,abdominalprotuberance,abdominalwallvaricosis,abdominalmass,abdominaltendernessorbounce,shiftingdullness FROM examination WHERE hepatitisType = ? LIMIT ?,20", new JsonArray().add(hepatitisType).add(page*20), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/HBV/:hepatitisType/:page", method = RouteMethod.GET)
    public Handler<RoutingContext> getHBVByType() {
        return ctx -> {
            String hepatitisType = ctx.request().getParam("hepatitisType");
            int page = Integer.parseInt(ctx.request().getParam("page"));
            if (StringUtils.isBlank(hepatitisType)) {
                LOGGER.error("hepatitisType is blank");
                JsonObject error = new JsonObject();
                error.put("error", "hepatitisType should not be blank");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT patientId,HBVDNA,HBsAg,HBsAb,HBeAg,HBeAb,HBcAb FROM examination WHERE hepatitisType = ? LIMIT ?,20", new JsonArray().add(hepatitisType).add(page*20), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }

    @RouteMapping(value = "/liverfunction/:hepatitisType/:page", method = RouteMethod.GET)
    public Handler<RoutingContext> getLiverfunctionByType() {
        return ctx -> {
            String hepatitisType = ctx.request().getParam("hepatitisType");
            int page = Integer.parseInt(ctx.request().getParam("page"));
            if (StringUtils.isBlank(hepatitisType)) {
                LOGGER.error("hepatitisType is blank");
                JsonObject error = new JsonObject();
                error.put("error", "hepatitisType should not be blank");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "SELECT patientId,GPT,GOT,TB,DB,albumin,globulin,TP,GGT,AP,AFP FROM examination WHERE hepatitisType = ? LIMIT ?,20", new JsonArray().add(hepatitisType).add(page*20), rs -> {
                    SQLUtil.close(conn.result());
                    if (rs.getRows().size() == 1) {
                        ctx.response().end(rs.getRows().get(0).encode());
                    } else {
                        JsonObject error = new JsonObject();
                        error.put("error", "Record not found");
                        ctx.response().setStatusCode(205).end(error.encode());
                    }
                });
            });
        };
    }
}
