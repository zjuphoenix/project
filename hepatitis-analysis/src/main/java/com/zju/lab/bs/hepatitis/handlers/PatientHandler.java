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
 * Created by wuhaitao on 2015/12/12.
 */
@RouteHandler("/patients")
public class PatientHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientHandler.class);

    @RouteMapping(method = RouteMethod.GET)
    public Handler<RoutingContext> getAll() {
        return ctx -> {
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());

            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "select * from patient", rs -> {
                    JsonArray patients = new JsonArray();
                    for (JsonObject row : rs.getRows()) {
                        patients.add(row);
                    }

                    SQLUtil.close(conn.result());
                    ctx.response().end(patients.encode());
                });
            });
        };
    }

    @RouteMapping(value = "/:id", method = RouteMethod.GET)
    public Handler<RoutingContext> fetch() {
        return ctx -> {
            String id = ctx.request().getParam("id");
            if (StringUtils.isBlank(id)) {
                LOGGER.error("ID is blank");
                JsonObject error = new JsonObject();
                error.put("error", "ID should not be blank");
                ctx.response().setStatusCode(205).end(error.encode());
            }

            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                SQLUtil.query(conn.result(), "select id, title, description from item where id = ?", new JsonArray().add(Integer.valueOf(id)), rs -> {
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
