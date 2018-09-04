package com.bobomee.android.paylib.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bobomee.android.paylib.interfaces.WxpayResultListener;
import com.bobomee.android.paylib.util.WxpayUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


/**
 * 微信支付回调
 *
 * @author BoBoMEe
 */
public class WXPayHandlerActivity extends AppCompatActivity implements IWXAPIEventHandler {

  private IWXAPI api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    api = WxpayUtil.getmIWXAPI();
    if (null != api)
    api.handleIntent(getIntent(), this);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    if (null != api)
    api.handleIntent(intent, this);
  }

  @Override public void onReq(BaseReq req) {
    finish();
  }

  // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
  @Override public void onResp(BaseResp baseResp) {
    WxpayResultListener sWxpayResultListener = WxpayUtil.getWxpayResultListener();
    //        String result = "";
    switch (baseResp.errCode) {
      case BaseResp.ErrCode.ERR_OK:
        //                result = "发送成功";
      {
        if (null != sWxpayResultListener && baseResp instanceof PayResp) {
          sWxpayResultListener.payResult((PayResp) baseResp);
        }
      }
      break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        //                result = "发送取消";
      {
        if (null != sWxpayResultListener && baseResp instanceof PayResp) {
          sWxpayResultListener.onCancel();
        }
      }
      break;
      case BaseResp.ErrCode.ERR_AUTH_DENIED:
        //                result = "发送被拒绝";
      default:
        //                result = "发送返回";
      {
        if (null != sWxpayResultListener && baseResp instanceof PayResp) {
          sWxpayResultListener.onError(baseResp.errCode);
        }
      }
      break;
    }
    finish();
  }
}
