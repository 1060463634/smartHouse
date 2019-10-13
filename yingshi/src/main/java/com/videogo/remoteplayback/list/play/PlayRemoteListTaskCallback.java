/**
 * @ProjectName: 民用软件平台软件
 * @Copyright: 2012 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2014-6-17 上午11:43:40
 * @Description: 本内容仅限于杭州海康威视数字技术股份有限公司内部使用，禁止转发.
 */
package com.videogo.remoteplayback.list.play;

import com.videogo.remoteplayback.RemoteFileInfo;
import com.videogo.openapi.bean.resp.CloudFile;

public interface PlayRemoteListTaskCallback {

    void playCloudPasswordError(CloudFile cloudFile);

    void playSucess();

    void playLocalPasswordError(RemoteFileInfo fileInfo);

    void playException(int errorCode, int retryCount, String detail);

    void playTaskOver(int type);

    void capturePictureSuccess(String filePath);

    void capturePictureFail(int errorCode);

    void startRecordSuccess(String filePath);

    void startRecordFail(int errorCode);

}
