--region *.lua
--Date
--此文件由[BabeLua]插件自动生成
module(..., package.seeall)
 


--重写error方法
local lua_error=error
local babe_error=function(...)
     local err=arg[1]
     local info="\r\n\t加密机前置: >>\r\n\t\t "..err.."\r\n"
     Core.XD_LOG(info)
     lua_error(info)
end 
error=babe_error

--重写print方法
local lua_print = print;  
local babe_output = function(...)  
    -- 输出到Visual Studio 调试窗口
    if decoda_output ~= nil then  
        local str = babe_tostring(...)
        decoda_output(str);  
    end  
    local date=os.date('%Y-%m-%d %H:%M:%S')
    -- 写日志文件                    
    local gbk= Core.XD_Utf8ToGbk(babe_tostring(...))
 
    local log=date.." "..gbk                      
    Core.XD_LOG(log)
    -- 输出到CMD窗口
    lua_print(log)
end  
print = babe_output;



--endregion
