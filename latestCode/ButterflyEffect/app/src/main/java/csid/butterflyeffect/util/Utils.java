package csid.butterflyeffect.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by hanseungbeom on 2018. 1. 28..
 */

public class Utils {

    public static byte[] intTobyte(int integer, ByteOrder order) {

        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
        buff.order(order);

        // 인수로 넘어온 integer을 putInt로설정
        buff.putInt(integer);

        return buff.array();
    }
}
