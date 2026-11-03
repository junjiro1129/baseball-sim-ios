package com.nakahomes.baseballsim;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;

import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Capacitor 8 本体には戻るボタンの処理が含まれていない
        // （BridgeActivity に onBackPressed の実装が無い）。
        // 何も登録しないと、端末の戻るボタンでアプリが即終了してしまう。
        //
        // ここでは Web 側の判定関数 window.__onAndroidBack() を直接呼ぶ:
        //   true  … モーダルを閉じた／画面を戻した = 処理済み → 何もしない
        //   false … これ以上戻る先が無い              → アプリを終了
        //
        // WebView の履歴(canGoBack)に依存しないため、
        // 端末や WebView バージョンによる履歴挙動の差に影響されない。
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bridge b = getBridge();
                WebView wv = (b != null) ? b.getWebView() : null;
                if (wv == null) {
                    finish();
                    return;
                }
                wv.evaluateJavascript(
                    "(function(){try{" +
                    "return !!(window.__onAndroidBack && window.__onAndroidBack());" +
                    "}catch(e){return false;}})()",
                    value -> {
                        if (!"true".equals(value)) {
                            finish();
                        }
                    }
                );
            }
        });
    }
}
