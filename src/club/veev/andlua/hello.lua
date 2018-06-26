--print("Hello, I'm hello.lua")

data = {}
data['id'] = 1
data['name'] = 'V'

function listener(arg1)
    print("lua 发出去消息的回调", arg1)
end

luabridge.call('fun', data)
luabridge.call('fun', "你好呀", listener)

function funn_handler(arg1, arg2)
    print("lua 收到了来自 java 消息", arg1, arg2)
    if (arg2)
    then
        arg2:call(678)
    end

end
luabridge.register("funn", funn_handler)

local Thread = luajava.bindClass('java.lang.Thread')

print(Thread:currentThread())
print(thread)