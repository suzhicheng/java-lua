--region *.lua
--Date
--此文件由[BabeLua]插件自动生成 

LCrypt={}

function LCrypt.process(tReq)
    local issuerCode = '_'..tReq.issuer_code
    local value = LDef.tEncryptor[issuerCode]:Crypt(tReq.index,tReq.scatter,tReq.vec,tReq.data,tReq.mode);     
   
    local t={}
    t['result']=value
    return t            
end

return LCrypt
--endregion
