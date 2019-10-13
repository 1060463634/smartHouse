/**
 * @ProjectName: 民用软件平台软件
 * @Copyright: 2012 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2014-6-6 上午8:57:54
 * @Description: 本内容仅限于杭州海康威视数字技术股份有限公司内部使用，禁止转发.
 */
package com.videogo.remoteplayback.list.querylist;

import java.util.List;

import com.videogo.remoteplayback.list.bean.CloudPartInfoFileEx;
import com.videogo.openapi.bean.resp.CloudPartInfoFile;

public interface QueryPlayBackListTaskCallback {

    void queryHasNoData();

    void queryOnlyHasLocalFile();

    void queryOnlyLocalNoData();

    void queryLocalException();

    void querySuccessFromCloud(List<CloudPartInfoFileEx> cloudPartInfoFileExs, int queryMLocalStatus, List<CloudPartInfoFile> cloudPartInfoFile);

    void querySuccessFromDevice(List<CloudPartInfoFileEx> cloudPartInfoFileExs, int position, List<CloudPartInfoFile> cloudPartInfoFile);

    void queryLocalNoData();

    void queryException();

    void queryTaskOver(int type, int queryMode, int queryErrorCode, String detail);

}
