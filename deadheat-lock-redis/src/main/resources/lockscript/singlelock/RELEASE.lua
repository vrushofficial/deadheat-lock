if redis.call('GET', KEYS[1]) == ARGV[1] then
    redis.call('PEXPIRE', KEYS[1], tonumber(ARGV[2]))
    return true
end
return false