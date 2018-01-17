/**  
 * All rights Reserved, Designed By Suixingpay.
 * @author: qiujiayu[qiu_jy@suixingpay.com] 
 * @date: 2018年1月17日 下午2:30:46   
 * @Copyright ©2018 Suixingpay. All rights reserved. 
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.jarvis.cache.demo.mapper.temp;

import com.jarvis.cache.annotation.Cache;

/**  
 * TODO
 * @author: qiujiayu[qiu_jy@suixingpay.com]
 * @date: 2018年1月17日 下午2:30:46
 * @version: V1.0
 * @review: qiujiayu[qiu_jy@suixingpay.com]/2018年1月17日 下午2:30:46
 */
public interface BaseMapper<T, PK> {

    @Cache(expire = 3600, expireExpression = "null == #retVal ? 600: 3600", key = "#target.getCacheName() +'-byid-' + #args[0]")
    T getById(PK id);
}
