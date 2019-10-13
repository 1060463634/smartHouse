/**
 * @ProjectName: 民用软件平台软件
 * @Copyright: 2012 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2014-6-5 下午4:41:40
 * @Description: 本内容仅限于杭州海康威视数字技术股份有限公司内部使用，禁止转发.
 */
package com.videogo.remoteplayback.list.querylist;

import com.videogo.remoteplayback.list.bean.ClickedListItem;

public interface RemoteListClickListener {

    void onMoreBtnClick(int position, boolean notExpand);

    void onListItemClick(ClickedListItem playClickItem);

}
