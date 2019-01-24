
module(..., package.seeall)



function exist(path)
    local file = io.open(path, "rb")
    if file then 
        file:close() 
    end
    return file ~= nil
end