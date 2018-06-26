package club.veev.andlua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class Main {

    public static void main(String[] args) {
//        System.out.println("Hello World!");

        Globals globals = AndLua.customGlobals(new LuaBridge());

        LuaBridge.registerHandler("fun", new LuaBridge.LuaHandler() {
            @Override
            public void handler(LuaValue data, LuaValue callback) {
                System.out.println("Java 收到了来自 lua 的消息: " + data);
                if (callback != null && callback != LuaValue.NIL) {
                    callback.call("aaa");
                }
            }
        });

        globals.loadfile("club/veev/andlua/lua.lua").call();
        globals.loadfile("club/veev/andlua/hello.lua").call();

        LuaBridge.callHandler("funn", "1", new LuaBridge.LuaCallback() {
            @Override
            public void call(Object data) {
                System.out.println("Java 发出去消息的回调: " + data);
            }
        });

        System.out.println("thread: " + globals.running);
    }
}
