package org.basecode.core.dict;

public interface Dict {
    /**
     * 支付状态
     */
    interface PAY {
        /**
         * 已经支付
         */
        //支付了
        String PAYED = "1";
        //未支付
        String UNPAY = "0";
    }

    /**
     * 有效状态
     */
    interface STATE {
        /**
         * 无效
         */
        //无效
        Integer OFF = 0;
        //有效
        Integer ON = 1;
    }

    // 代码使用
    // Dict.PAY.PAYED
}
