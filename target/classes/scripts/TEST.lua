--region *.lua
--Date
 
 
 --[[
--导出密钥
{"trans_data": 
{
	 "oper":"Export",
     "issuer_code": "04809020",
     "scatter":"04688820FFFFFFFF",
     "index":402
 }
 }
 
-- ]]

--[[
--计算MAC
{"trans_data":
   {  
    "data":"00006D480000000002123456123456",
    "dekval":"C2B65E4A00088000",
    "index":402,
    "issuer_code":"04809020",
    "oper":"CalMac",
    "scatter":"4792600010000004",
    "vec":"0000000000000000"
    }
} 
-- ]]


--[[
--加密解密
{"trans_data": 
{
		"oper": "Crypt",
		"issuer_code": "04809020",
		"scatter": "4792600010000004",
		"vec": "0000000000000000",
		"index":402,
		"data": "04DC000C06010204",
		"mode":0
	}
}
-- ]]


function dispatch(req)
	return "test";
end


--endregion
