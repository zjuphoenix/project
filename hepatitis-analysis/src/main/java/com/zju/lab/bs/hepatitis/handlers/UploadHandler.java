package com.zju.lab.bs.hepatitis.handlers;

import com.zju.lab.bs.hepatitis.utils.AppUtil;
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
 * Created by wuhaitao on 2015/12/10.
 */
@RouteHandler("/api/upload")
public class UploadHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);

    @RouteMapping(method = RouteMethod.POST)
    public Handler<RoutingContext> upload() {
        return ctx -> {

            Set<FileUpload> files = ctx.fileUploads();
            String filename = "";
            for (FileUpload file : files) {
                String path = file.uploadedFileName();
                LOGGER.debug("upload path : {}", path);
                filename = path.substring(path.lastIndexOf("\\") + 1);
            }

            JsonObject file = new JsonObject();
            file.put("filename", filename);
            ctx.response().end(file.encode());
        };
    }

    @RouteMapping(value = "/:filename", method = RouteMethod.GET)
    public Handler<RoutingContext> showFile() {
        return ctx -> {
            String filename = ctx.request().getParam("filename");
            // prepend upload dir
            filename = AppUtil.getUploadDir() + "\\" + filename;

            ctx.response().sendFile(filename, res -> {
                if (res.failed()) {
                    LOGGER.error(res.cause().getMessage(), res.cause());
                    ctx.fail(res.cause());
                }
            });
        };
    }

}
