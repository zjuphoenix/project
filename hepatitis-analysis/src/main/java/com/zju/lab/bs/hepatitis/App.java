package com.zju.lab.bs.hepatitis;

import com.zju.lab.bs.hepatitis.verticle.Server;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by wuhaitao on 2015/12/10.
 */
public class App {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        // 设置工作线程
        options.setInternalBlockingPoolSize(20);
        //options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle(Server.class.getName());

        /** 添加钩子函数,保证vertx的正常关闭 */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            vertx.close();
            System.out.println("server stop success!");
        }));
    }
}
