--region *.lua
--Date
--此文件由[BabeLua]插件自动生成

LExport={}
function LExport.process(tReq)

    local issuerCode = '_'..tReq.issuer_code
    local key = LDef.tEncryptor[issuerCode]:ExportKey(tReq.index,tReq.scatter);

    local t={}
    t['result']=key
    return t
    
end

return LExport
--endregion
