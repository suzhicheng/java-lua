--region *.lua
--Date
--此文件由[BabeLua]插件自动生成 
LRoute={}


LRoute.tRoute = {
	{ oper="Export"		    , name='导出'     },
    { oper="CalMac"		    , name='计算MAC'  },
    { oper="Crypt"		    , name='加解密'   },
}
function LRoute.dispatch(funName, tReq) 


    for k, v in pairs(LRoute.tRoute) do
        if v.oper == funName then 
		      -- 加载处理方法
		      local method = require(string.format("%s.%s", "pos.oper", v.oper))
			  local tResult = {}
			  tResult = method.process(tReq)			-- 执行业务逻辑
			  return tResult	-- 返回结果
        end
    end
end 
return LRoute
--endregion
