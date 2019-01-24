--region *.lua
--Date
--此文件由[BabeLua]插件自动生成    
LCalMac={}

function LCalMac.process(tReq)

    local issuerCode = '_'..tReq.issuer_code
  
    local mac = LDef.tEncryptor[issuerCode]:CalMac(tReq.index,tReq.scatter,tReq.vec,tReq.data,tReq.dekval);
    local t={}
    t['result']=mac  
    return t

end

return LCalMac


--endregion
