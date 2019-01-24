--region *.lua
--Date
--此文件由[BabeLua]插件自动生成
require("pos.route")
module(..., package.seeall)
tRoute = {
	{ oper="Export"		    , name='导出'     },
    { oper="CalMac"		    , name='计算MAC'  },
    { oper="Crypt"		    , name='加解密'   },
}
local function FindProcessModule(modname, funName)
    for k,v in pairs(tRoute) do
        if v.oper == funName then
            -- 业务处理模块名
            local submod = string.format("%s.%s", modname, funName)

            -- "."替换成"/"
            submod = string.gsub(submod, "%.", "/")

            -- 问号替换成模块名
            local path = string.gsub(package.path, "%?", submod)
            
            -- 分号";"分割
            local files = string.split(path, ";")

            for k,file in pairs(files) do
                if utils.file.exist(file) then
                    return v
                end
            end

            error('接口未实现')
        end
    end
end


function dispatch(funName, tReq)
    -- 查找指令对应的处理方法
    local route = FindProcessModule("pos.oper", funName)

    if route ~= nil then
        -- 加载处理方法
        local method = require(string.format("%s.%s", "pos.oper", route.oper))
	    local tResult = {}
	    tResult = method.process(tReq)			-- 执行业务逻辑
	    return tResult	-- 返回结果
    end
end



--endregion
