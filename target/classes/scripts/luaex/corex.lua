--region *.lua
--Date
 
      
require("protocol.zjb")
require("protocol.jtb")
require("Core")
 



Encryptor = {} 


function Encryptor:new(name,protocol,addr,port)
    local o= o or{
       	name=name,
	   	protocol=protocol,
		addr = addr,
		port = port,
		export_key_protocol_func = {
    		["ZJB"] = LZJB.export_key,         
			["JTB"] = LJTB.export_key,         
		},
  		cal_mac_protocol_func = {
    		["ZJB"] = LZJB.cal_mac,         
			["JTB"] = LJTB.cal_mac,        
		},
		crypt_protocol_func = {
    		["ZJB"] = LZJB.crypt,         
			["JTB"] = LJTB.crypt,        
		},
		--luaf = luajava.newInstance("com.emptech.platform.LuaFunction")
    }
    setmetatable(o,Encryptor)
	self.__index=self 
    return  o
end  

 

function Encryptor:Transmit(send)  
    local recv =Core.Transmit(self.addr,self.port,send)
    if recv == '' or nil==recv then
        error("请求加密机前置处理失败")
    end
    return recv
end

function Encryptor:DESCrypt(data,key,mode,init) 
    local recv = ""
    if mode == "E_DES_ECB_DE" then
    	recv = Core.DesDeCrypt(data,key,mode,init)
    else 
    	recv = Core.DesEnCrypt(data,key,mode,init)
	end 
    print(recv)
    if recv == '' then
        error('')
    end
    return recv
end

function Encryptor:ANSI9x19MAC(vec,key,data) 
    local recv =Core.ANSI9x19MAC(vec,key,data)
	 
    if recv == '' then
        error('')
    end
    return recv
end

function Encryptor:ANSI9x9MAC(vec,key,data) 
   --[[  local recv =Core.XD_ANSI9x9MAC(vec,key,data)
	
    print(recv)
    if recv == '' then
        error('')
    end
    return recv--]]
end

function Encryptor:XOR(data1,data2) 
    local recv =Core.Xor(data1,data2)
    print(recv)
    if recv == '' then
        error('')
    end
    return recv
end

function Encryptor:ExportKey(index,scatter)   
    if self.export_key_protocol_func[self.protocol] then
         return self.export_key_protocol_func[self.protocol](self,index,scatter)
    else
        error('')
    end
end

function Encryptor:CalMac(index,scatter,vec,data,dekval)   
	 
    if self.cal_mac_protocol_func[self.protocol] then
         return self.cal_mac_protocol_func[self.protocol](self,index,scatter,vec,data,dekval)
    else
        error('')
    end
end

function Encryptor:Crypt(index,scatter,vec,data,mode)   
    if self.crypt_protocol_func[self.protocol] then
         return self.crypt_protocol_func[self.protocol](self,index,scatter,vec,data,mode)
    else
        error('')
    end
end

return Encryptor
--endregion
