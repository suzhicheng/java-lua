package.path = package.path .. ';./scripts/?.lua;?.lua;./?.lua'
json=require("utils.JSON")



--[[ 
print(package.path)
print(package.cpath)
print("12345") 
--require("Core")
print("12345")
local val="{\"trans_data\": \"test\"}"
local tVal=json:decode(val)
print(tVal['trans_data'])
--]]

 

LDispatch={}

function LDispatch.dispatch(req)
    local tReq=json:decode(req).trans_data;
   	print(tReq['data'])
   	for k,v in pairs(tReq) do
   		print(k.."\t"..v)
   	end 
    local t={}
    local status, val = pcall(LRoute.dispatch, tReq.oper,tReq)
    
    if status == false then
        print("ERROR:"..val)
        local err={}
        err['result']=""
        err['msg']=val
        t['trans_data']=err
    else 
        t['trans_data']=val 
    end
     
    for k,v in pairs(t['trans_data']) do
          print(k.."\t\t"..v)
    end
    return json:encode(t)
end 

return LDispatch