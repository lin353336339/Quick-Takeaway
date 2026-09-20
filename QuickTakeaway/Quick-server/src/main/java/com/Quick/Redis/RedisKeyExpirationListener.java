//package com.Quick.Redis;
//
//import com.Quick.constant.MessageConstant;
//import com.Quick.dto.OrdersCancelDTO;
//import com.Quick.dto.OrdersRejectionDTO;
//import com.Quick.entity.Orders;
//import com.Quick.mapper.OrderMapper;
//import com.Quick.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private RedisMessageListenerContainer container;
//
//    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
//        super(listenerContainer);
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try{
//            String expiredKey = message.toString();//获取所有key
//            if(expiredKey.endsWith("_LANSHAN")){//判断后缀是否�?_LANSHAN'
//                System.err.print("我得到的key:"+expiredKey);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    private void CancelTimeExpiration(String key) {
//        Integer value = (Integer) redisTemplate.opsForValue().get(key);
//        if (value == Orders.PENDING_PAYMENT){
//            Long result = Long.valueOf(key.split(":", 2)[1]); // 只分割一�?
//            OrdersCancelDTO ordersRejectionDTO = new OrdersCancelDTO();
//            ordersRejectionDTO.setId(result);
//            ordersRejectionDTO.setCancelReason(MessageConstant.TIME_CANCEL_ORDER);
//            orderService.cancel(ordersRejectionDTO);
//        }
//    }
//    private void CompleteTimeExpiration(String key) {
//        Integer value = (Integer) redisTemplate.opsForValue().get(key);
//        if (value == Orders.DELIVERY_IN_PROGRESS){
//            Long result = Long.valueOf(key.split(":", 2)[1]); // 只分割一�?
//            orderService.complete(result);
//        }
//    }
//}
//
//
//
//
