/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.client.android.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.zxing.client.android.CaptureActivityInterface;

/**
 * Manufactures Android-specific handlers based on the barcode content's type.
 * <br/><br/>
 * 
 * 生产XXXResultHandler的工厂方法，将扫描的结果分为不同的类型，根据对应的类型返回对应处理类来进行下一步处理。<br/>
 * 具体的类型参考core文档中的ParsedResultType。<br/>
 * 所有的XXXResultHandler都继承自ResultHandler虚拟类
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    public static ResultHandler makeResultHandler(CaptureActivityInterface activity, Result rawResult) {
        ParsedResult result = parseResult(rawResult);
        switch (result.getType()) {
            case URI:
                return new URIResultHandler(activity.getActivity(), result);
            default: // 默认是普通文本
                return new TextResultHandler(activity.getActivity(), result, rawResult);
        }
    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }
}
