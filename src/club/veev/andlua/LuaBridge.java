package club.veev.andlua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LuaBridge extends VarArgFunction {
    static final int INIT           = 0;
    static final int REGISTER       = 1;
    static final int CALL           = 2;
    static final int REMOVE         = 3;
    static final int HANDLER        = 4;

    static final String[] NAMES = {
            "register",
            "call",
            "remove",
            "handler"
    };

    public LuaBridge() {

    }

    @Override
    public Varargs invoke(Varargs args) {
        try {
            switch (opcode) {
                case INIT:
                    // LuaValue modname = args.arg1();
                    LuaValue env = args.arg(2);
                    LuaTable t = new LuaTable();
                    bind( t, this.getClass(), NAMES, REGISTER );
                    env.set("luabridge", t);
                    env.get("package").get("loaded").set("luabridge", t);
                    break;
                case REGISTER:
                    LuaValue luaFunction = null;
                    switch (args.narg()) {
                        case 2:
                            luaFunction = args.arg(2);
                        case 1:
                            String name = args.arg(1).tojstring();
                            if (luaFunction != null) {
                                Set<LuaValue> valueSet = mLuaHandler.get(name);
                                if (valueSet != null) {
                                    valueSet.add(luaFunction);
                                } else {
                                    valueSet = new HashSet<>();
                                    valueSet.add(luaFunction);
                                    mLuaHandler.put(name, valueSet);
                                }
                            }
                        default:
                            break;
                    }
                    break;
                case CALL:
                    LuaValue callback = NIL, data = NIL;
                    switch (args.narg()) {
                        case 3:
                            callback = args.arg(3);
                        case 2:
                            data = args.arg(2);
                        case 1:
                            String name = args.arg(1).tojstring();
                            Set<LuaHandler> set = mJavaHandler.get(name);
                            if (set != null) {
                                for (LuaHandler h : set) {
                                    h.handler(data, callback);
                                }
                            }
                        default:
                            break;
                    }
                    break;
                case REMOVE:
                    break;
                case HANDLER:
                    System.out.println("Handler: " + args.arg1());
                    break;
                default:
                    System.out.println();
                    System.out.println("====== Start ======");

                    int narg = args.narg();
                    for (int i = 1; i <= narg; i++) {
                        System.out.println("index: " + i + "\targ: " + args.arg(i) + " " + args.arg(i).getClass().getSimpleName());
                    }

                    System.out.println("======  End  ======");
                    System.out.println();
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return NIL;
    }

    private static Map<String, Set<LuaHandler>> mJavaHandler = new HashMap<>();
    private static Map<String, Set<LuaValue>> mLuaHandler = new HashMap<>();

    public static void registerHandler(String name, LuaHandler handler) {
        Set<LuaHandler> set = mJavaHandler.get(name);
        if (set == null) {
            set = new HashSet<>();
            set.add(handler);
            mJavaHandler.put(name, set);
        } else {
            set.add(handler);
        }
    }

    public static void callHandler(String name) {
        callHandler(name, null);
    }

    public static void callHandler(String name, String data) {
        callHandler(name, data, null);
    }

    public static void callHandler(String name, String data, LuaCallback callback) {
        Set<LuaValue> set = mLuaHandler.get(name);
        if (set != null) {
            for (LuaValue f : set) {
                f.call(CoerceJavaToLua.coerce(data), CoerceJavaToLua.coerce(callback));
            }
        }
    }

    public interface LuaHandler{
        void handler(LuaValue data, LuaValue callback);
    }

    public interface LuaCallback {
        void call(LuaValue data);
    }
}
