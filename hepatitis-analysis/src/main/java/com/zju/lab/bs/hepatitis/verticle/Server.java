package com.zju.lab.bs.hepatitis.verticle;

import com.zju.lab.bs.hepatitis.annotations.RouteHandler;
import com.zju.lab.bs.hepatitis.annotations.RouteMapping;
import com.zju.lab.bs.hepatitis.annotations.RouteMethod;
import com.zju.lab.bs.hepatitis.security.APIInterceptorHandler;
import com.zju.lab.bs.hepatitis.security.FormLoginHandlerImpl;
import com.zju.lab.bs.hepatitis.utils.AppUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by wuhaitao on 2015/12/10.
 */
public class Server extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    // Scan handlers from package 'com.zju.lab.bs.hepatitis.handlers.*'
    private static final Reflections reflections = new Reflections("com.zju.lab.bs.hepatitis.handlers");

    private Integer port = AppUtil.configInt("port");

    protected Router router;

    @Override
    public void start() throws Exception {

        LOGGER.debug("Start server at port {} .....", port);

        router = Router.router(vertx);

        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create().setUploadsDirectory(AppUtil.getUploadDir()));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        AuthProvider authProvider = getAuthProvider();
        router.route().handler(UserSessionHandler.create(authProvider));

        router.route("/api/*").handler(getAPIInterceptorHandler());

        // registerHandlers
        registerHandlers();

        // Handles the actual login
        router.route("/login").handler(new FormLoginHandlerImpl(authProvider));
        // Implement logout
        router.route("/logout").handler(context -> {
            context.clearUser();
            // Redirect back to the index page
            context.response().putHeader("location", "/").setStatusCode(302).end();
        });

        // Must be the latest handler to register
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(port);

    }

    protected AuthProvider getAuthProvider() {
        return JDBCAuth.create(AppUtil.getJdbcClient(vertx));
    }

    protected AuthHandler getAPIInterceptorHandler() {
        return new APIInterceptorHandler(getAuthProvider());
    }

    private void registerHandlers() {
        LOGGER.debug("Register available request handlers...");

        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);
        for (Class<?> handler : handlers) {
            try {
                registerNewHandler(handler);
            } catch (Exception e) {
                LOGGER.error("Error register {}", handler);
            }
        }
    }

    private void registerNewHandler(Class<?> handler) throws Exception {
        String root = "";
        if (handler.isAnnotationPresent(RouteHandler.class)) {
            RouteHandler routeHandler = handler.getAnnotation(RouteHandler.class);
            root = routeHandler.value();
        }
        Object instance = handler.newInstance();
        Method[] methods = handler.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RouteMapping.class)) {
                RouteMapping mapping = method.getAnnotation(RouteMapping.class);
                RouteMethod routeMethod = mapping.method();
                String url = root + mapping.value();
                Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method.invoke(instance);
                LOGGER.debug("Register New Handler -> {}:{}", routeMethod, url);
                switch (routeMethod) {
                    case POST:
                        router.post(url).handler(methodHandler);
                        break;
                    case PUT:
                        router.put(url).handler(methodHandler);
                        break;
                    case DELETE:
                        router.delete(url).handler(methodHandler);
                        break;
                    case GET: // fall through
                    default:
                        router.get(url).handler(methodHandler);
                        break;
                }
            }
        }
    }

}
