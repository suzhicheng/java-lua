--region *.lua
--Date
--此文件由[BabeLua]插件自动生成 

LJTB={} 

function LJTB.export_key(self,index,scatter)
     local keyIndex = "K"..string.format("%02X",index)
     local len = #scatter
     local dataF = string.rep("F", len)
     local scatterNegation = self:XOR(scatter, dataF)
	 
     local key = "U1 X 0 01 109"..keyIndex.." 0 01"..string.format("%03d", len)..scatter..scatterNegation
     key = self:Transmit(key)
     
     if string.sub(key, 3, 4) ~= "00" then
         return string.sub(key, 3, 4)
     else
         key = string.sub(key, 8, 39)
     end
     
     return key
end 

function LJTB.cal_mac(self,index,scatter,vec,data,dekval)
     local keyIndex = "K"..string.format("%03X", index)
     local len = #scatter
     local dataF = string.rep("F", len)
     local scatterNegation = self:XOR(scatter, dataF)
	 
     local key = "U1 X 0 01 109"..keyIndex.." 0 01"..string.format("%03d", len)..scatter..scatterNegation
     key = self:Transmit(key)
     
     if string.sub(key, 3, 4) ~= "00" then
         return string.sub(key, 3, 4)
     else
         key = string.sub(key, 8, 39)
     end
     
     if dekval ~= "" and dekval ~= nil then
         key = self:DESCrypt(dekval, key, LDef.ECrypt.E_DES_ECB_EN, vec)
     end
     
  	 local value = self:ANSI9x19MAC(vec, key, data)
  	 return value
end 

function LJTB.crypt(self,index,scatter,vec,data,mode)

     local keyIndex = "K"..string.format("%03X",index)
     local len = #scatter
     local dataF = string.rep("F", len)
     local scatterNegation = self:XOR(scatter, dataF)
	 
     local key = "U1 X 0 01 109"..keyIndex.." 0 01"..string.format("%03d", len)..scatter..scatterNegation
     key = self:Transmit(key)
     
     if string.sub(key, 3, 4) ~= "00" then
         return string.sub(key, 3, 4)
     else
         key = string.sub(key, 8, 39)
     end
     
     local value = self:DESCrypt(data, key, mode, vec)    
     return value
end 

return LJTB
--endregion
