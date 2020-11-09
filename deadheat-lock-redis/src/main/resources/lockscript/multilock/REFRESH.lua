for _, key in pairs(KEYS) do
    local value = redis.call('GET', key)
    if (value == nil or value ~= ARGV[1]) then
        return false
    end
end
for _, key in pairs(KEYS) do
    redis.call('PEXPIRE', key, ARGV[2])
end
return true