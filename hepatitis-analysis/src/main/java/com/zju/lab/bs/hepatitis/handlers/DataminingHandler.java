package com.zju.lab.bs.hepatitis.handlers;

import com.zju.lab.bs.hepatitis.annotations.RouteHandler;
import com.zju.lab.bs.hepatitis.annotations.RouteMapping;
import com.zju.lab.bs.hepatitis.annotations.RouteMethod;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by wuhaitao on 2015/12/14.
 */
@RouteHandler("/datamining")
public class DataminingHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataminingHandler.class);

    @RouteMapping(value = "/analyse", method = RouteMethod.POST)
    public Handler<RoutingContext> uploadFileToAnalyse() {
        return ctx -> {

            Set<FileUpload> files = ctx.fileUploads();
            String filename = "";
            for (FileUpload file : files) {
                String path = file.uploadedFileName();
                LOGGER.debug("upload path : {}", path);
                filename = path.substring(path.lastIndexOf("\\") + 1);
            }

            JsonObject result = new JsonObject();
            result.put("result", "病毒性乙型肝炎");
            ctx.response().end(result.encode());
        };
    }
}
