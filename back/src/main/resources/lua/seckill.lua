-- src/main/resources/lua/seckill.lua

-- 1. 参数列表
-- KEYS[1]: nil
-- ARGV[1]: 优惠券ID (voucherId)
-- ARGV[2]: 用户ID (userId)
-- ARGV[3]: 订单ID (orderId)

local voucherId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

-- 2. 构造库存和订单的Key
local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order:' .. voucherId

-- 3. 判断库存是否充足
if(tonumber(redis.call('get', stockKey)) <= 0) then
    return 1 -- 返回1代表库存不足
end

-- 4. 判断用户是否已下单
if(redis.call('sismember', orderKey, userId) == 1) then
    return 2 -- 返回2代表重复下单
end

-- 5. 扣减库存、记录用户
redis.call('incrby', stockKey, -1)
redis.call('sadd', orderKey, userId)

-- 6. 【重要】将订单信息发送到消息队列
-- 我们使用Redis的Stream作为消息队列
-- xadd stream.orders * voucherId 1 userId 1 orderId 1
redis.call('xadd', 'stream.orders', '*', 'voucherId', voucherId, 'userId', userId, 'orderId', orderId)

return 0 -- 返回0代表成功