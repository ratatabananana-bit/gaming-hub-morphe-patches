package app.ratbanana.extension.gaminghub;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Builds Gaming Hub's own server response models, so that on-device play history goes through
 * exactly the same code paths and screens as a response from Samsung's server.
 * <p>
 * The model classes keep their names for JSON parsing, but the extension cannot link against
 * them, so they are created reflectively by constructor shape.
 */
final class HistoryModels {

    private static final String MODEL_PACKAGE = "com.samsung.android.game.gamehome.network.gamelauncher.model.";
    private static final String DASHBOARD_ITEM = MODEL_PACKAGE + "dashboard.DashBoardItem";
    private static final String RANK_ITEM = MODEL_PACKAGE + "dashboard.RankItem";
    private static final String PLAY_LOG = MODEL_PACKAGE + "dashboard.PlayLog";
    private static final String GAME_TYPE = MODEL_PACKAGE + "main.constant.GameType";

    static final String SUCCESS_RESULT_CODE = "000";
    private static final String INSTALLED_GAME_TYPE = "INSTALLATION";

    private final ClassLoader classLoader;

    HistoryModels(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    Object dashBoardItem(String statType, String statData, String statDate, int rank,
                         String name, String packageName) throws ReflectiveOperationException {
        return create(load(DASHBOARD_ITEM),
                statType, statData, "", statDate, rank, name, packageName, "", "",
                enumConstant(load(GAME_TYPE), INSTALLED_GAME_TYPE), "");
    }

    Object rankItem(String rankType, List<Object> dashBoardItems) throws ReflectiveOperationException {
        return create(load(RANK_ITEM), rankType, dashBoardItems);
    }

    Object playLog(String statType, Object total, List<Object> userItems,
                   List<Object> otherItems) throws ReflectiveOperationException {
        return create(load(PLAY_LOG), statType, total, userItems, otherItems);
    }

    /**
     * @param responseType Declared result type of the API call: either the model itself or
     *                     Retrofit's {@code Response<Model>} wrapper.
     * @param modelArgs    Constructor arguments of the model.
     * @return The response in the declared result type.
     */
    static Object response(Type responseType, Object... modelArgs) throws ReflectiveOperationException {
        if (responseType instanceof ParameterizedType) {
            ParameterizedType wrapperType = (ParameterizedType) responseType;
            Class<?> wrapper = (Class<?>) wrapperType.getRawType();
            Object body = create((Class<?>) wrapperType.getActualTypeArguments()[0], modelArgs);
            return successResponse(wrapper, body);
        }
        if (responseType instanceof Class) {
            return create((Class<?>) responseType, modelArgs);
        }
        throw new ClassNotFoundException("Unsupported response type " + responseType);
    }

    /** {@code Response.success(body)}: the only static factory of the wrapper with one parameter. */
    private static Object successResponse(Class<?> wrapper, Object body) throws ReflectiveOperationException {
        for (Method method : wrapper.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())
                    && method.getReturnType() == wrapper
                    && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0] == Object.class) {
                method.setAccessible(true);
                return method.invoke(null, body);
            }
        }
        throw new NoSuchMethodException("No success factory in " + wrapper.getName());
    }

    private Class<?> load(String className) throws ClassNotFoundException {
        return Class.forName(className, true, classLoader);
    }

    private static Object enumConstant(Class<?> enumType, String name) throws NoSuchFieldException {
        for (Object constant : enumType.getEnumConstants()) {
            if (((Enum<?>) constant).name().equals(name)) {
                return constant;
            }
        }
        throw new NoSuchFieldException(enumType.getName() + "." + name);
    }

    /** Calls the public constructor whose parameters accept the given arguments. */
    private static Object create(Class<?> type, Object... args) throws ReflectiveOperationException {
        for (Constructor<?> constructor : type.getConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) {
                return constructor.newInstance(args);
            }
        }
        throw new NoSuchMethodException("No matching constructor in " + type.getName());
    }

    private static boolean accepts(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            Class<?> parameter = boxed(parameterTypes[i]);
            if (args[i] == null ? parameterTypes[i].isPrimitive() : !parameter.isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> boxed(Class<?> type) {
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == boolean.class) return Boolean.class;
        return type;
    }
}
