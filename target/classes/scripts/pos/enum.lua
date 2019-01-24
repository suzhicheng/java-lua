--region *.lua
--Date
--此文件由[BabeLua]插件自动生成
TCrypt = {
	E_DES_ECB_EN = 0,  --DES ECB模式加密
	E_DES_ECB_DE = 1,  --DES ECB模式解密
	E_DES_CBC_EN = 2,  --DES CBC模式加密
	E_DES_CBC_DE = 3,  --DES CBC模式解密
	E_RSA_PUB_EN = 4,  --RSA 公钥加密
	E_RSA_PUB_DE = 5,  --RSA 公钥解密
	E_RSA_PRI_EN = 6,  --RSA 私钥加密
	E_RSA_PRI_DE = 7   --RSA 私钥解密
};

function CreatEnumTable(tbl, index) 
    local enumtbl = {} 
    local enumindex = index or 0 
    for i, v in ipairs(tbl) do 
        enumtbl[v] = enumindex + i - 1 
    end 
    return enumtbl 
end


--endregion
