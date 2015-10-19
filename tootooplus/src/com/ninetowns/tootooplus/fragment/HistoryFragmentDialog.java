package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.CustomArrayAdapter;
import com.ninetowns.tootooplus.adapter.HistoryAdapter;
import com.ninetowns.tootooplus.bean.ActivityHistoryBean;
import com.ninetowns.tootooplus.bean.CommentHistoryBean;
import com.ninetowns.tootooplus.bean.HistoryBean;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.widget.listview.multicolumn.HorizontalListView;

/**
 * 
 * @ClassName: HistoryFragmentDialog
 * @Description: 历史记录
 * @author wuyulong
 * @date 2015-4-11 上午11:06:25
 * 
 */
public class HistoryFragmentDialog extends Fragment implements OnClickListener,
		OnItemClickListener {
	private View historyView;
	@ViewInject(R.id.history_list)
	private ListView mListView;// 列表

	@ViewInject(R.id.ll_history_view)
	private LinearLayout mLLHishtoryView;// 历史记录界面
	@ViewInject(R.id.ct_clear_history)
	private CheckedTextView mCTClearHistory;
	private String type;
	private HistoryAdapter historyAdapter;
	private List<HistoryBean> listHistory = new ArrayList<HistoryBean>();
	private View mFLHistory;
	private View mRefresh;
	private List<ActivityHistoryBean> activityHistory = new ArrayList<ActivityHistoryBean>();
	private List<CommentHistoryBean> commentHistoryList = new ArrayList<CommentHistoryBean>();
	private List<String> hotList = new ArrayList<String>();

	public HistoryFragmentDialog() {
	}

	public HistoryFragmentDialog(String type, View mFLHistory, View mRefresh) {
		this.type = type;
		this.mFLHistory = mFLHistory;
		this.mRefresh = mRefresh;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		historyView = inflater.inflate(R.layout.search_or_history, null);
		ViewUtils.inject(this, historyView);
		justWishheader();
		setOnViewClickListener();
		return historyView;
	}

	/**
	 * 
	 * @Title: setHeadTitle
	 * @Description: 设置数据
	 * @param
	 * @return
	 * @throws
	 */
	public void setHeadTitle(final String type) {

		if ((NetworkUtil.isNetworkAvaliable(getActivity()))) {
			// 显示进度
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.TYPE, type);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.PAGE_SIZE, "100");
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.PAGE, "1");
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.HOT_SEARCH,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jobj = new JSONObject(jsonStr);
								if (jobj.has("Status")) {
									String status = jobj.getString("Status");
									if (status.equals("1")) {

									} else {
										return;

									}

								}
								if (jobj.has("Data")) {
									JSONObject dataObj = new JSONObject(jobj
											.getString("Data"));

									if (dataObj.has("List")) {
										String strList = dataObj
												.getString("List");
										JSONArray array = new JSONArray(strList);
										if (array != null && array.length() > 0) {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array
														.get(i);
												hotList.add(obj
														.getString("Keyword"));
											}
										}

									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (hotList.size() > 0) {
								hotList.add(0, "热门搜索： ");
								View headerView = LayoutInflater.from(
										getActivity()).inflate(
										R.layout.horizatial_listview, null,
										true);
								mListView.addHeaderView(headerView);
								HorizontalListView horiontallListView = (HorizontalListView) headerView
										.findViewById(R.id.horiontalListview);
								CustomArrayAdapter hotAdatper = new CustomArrayAdapter(
										getActivity(), hotList);
								horiontallListView.setAdapter(hotAdatper);
								hotAdatper.notifyDataSetChanged();
								horiontallListView
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> parent,
													View view, int position,
													long id) {
												if (position != 0) {
													if (!TextUtils
															.isEmpty(type)) {
														if (type.equals("0")) {// 心愿

															searchHistory(
																	hotList.get(position),
																	true);

														} else if (type
																.equals("1")) {// 点评
															searchCommentHistory(
																	hotList.get(position),
																	true);
														} else {// 活动
															searchActHistory(
																	hotList.get(position),
																	true);
														}

													}
												}

											}

										});
								justWishOrActivityOrComment();

							} else {
								justWishOrActivityOrComment();
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							justWishOrActivityOrComment();
							ComponentUtil
									.showToast(
											getActivity(),
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}
					});

		} else {
			justWishOrActivityOrComment();
			ComponentUtil.showToast(getActivity(), this.getResources()
					.getString(R.string.errcode_network_response_timeout));
		}

	}

	/**
	 * 
	 * @Title: searchHistoryData
	 * @Description: 搜索是否有数据，
	 * @param
	 * @return
	 * @throws
	 */
	private void searchWishHistoryData() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			try {
				List<HistoryBean> locallistHistory = db.findAll(Selector
						.from(HistoryBean.class));
				if (locallistHistory != null && locallistHistory.size() > 0) {
					listHistory.addAll(locallistHistory);
					mLLHishtoryView.setVisibility(View.VISIBLE);
					initWishHistoryDataAdapter();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
				} else {
					initWishHistoryDataAdapter();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
					mLLHishtoryView.setVisibility(View.INVISIBLE);
				}
			} catch (DbException e) {
				LogUtil.error("db", "数据库出现异常");
				e.printStackTrace();
				return;
			}
		} else {
			initWishHistoryDataAdapter();
			mLLHishtoryView.setVisibility(View.INVISIBLE);
			mRefresh.setVisibility(View.GONE);
			mFLHistory.setVisibility(View.VISIBLE);
			mLLHishtoryView.setVisibility(View.INVISIBLE);
			LogUtil.error("db", "=null");
		}

	}

	/**
	 * @Title: initWishHistoryDataAdapter
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void initWishHistoryDataAdapter() {
		historyAdapter = new HistoryAdapter(getActivity(), listHistory);
		mListView.setAdapter(historyAdapter);
		historyAdapter.notifyDataSetChanged();

	}

	/**
	 * 
	 * @Title: searchHistoryData
	 * @Description: 搜索点评
	 * @param
	 * @return
	 * @throws
	 */
	private void searchCommentHistoryData() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			try {
				List<CommentHistoryBean> commentlist = db.findAll(Selector
						.from(CommentHistoryBean.class));
				if (commentlist != null && commentlist.size() > 0) {
					commentHistoryList.addAll(commentlist);
					mLLHishtoryView.setVisibility(View.VISIBLE);
					initHistoryCommentAdapter();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
				} else {
					initHistoryCommentAdapter();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
					mLLHishtoryView.setVisibility(View.INVISIBLE);
				}
			} catch (DbException e) {
				LogUtil.error("db", "数据库出现异常");
				e.printStackTrace();
				return;
			}
		} else {
			initHistoryCommentAdapter();
			mLLHishtoryView.setVisibility(View.INVISIBLE);
			mRefresh.setVisibility(View.GONE);
			mFLHistory.setVisibility(View.VISIBLE);
			mLLHishtoryView.setVisibility(View.INVISIBLE);
			LogUtil.error("db", "=null");
		}

	}

	/**
	 * @Title: initHistoryCommentAdapter
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void initHistoryCommentAdapter() {
		historyAdapter = new HistoryAdapter(getActivity(), commentHistoryList);
		mListView.setAdapter(historyAdapter);
		historyAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @Title: searchActivityHistoryData
	 * @Description: 搜索活动
	 * @param
	 * @return
	 * @throws
	 */
	private void searchActivityHistoryData() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			try {
				List<ActivityHistoryBean> localActList = db.findAll(Selector
						.from(ActivityHistoryBean.class));
				if (localActList != null && localActList.size() > 0) {
					activityHistory.addAll(localActList);
					mLLHishtoryView.setVisibility(View.VISIBLE);
					historyAdapter = new HistoryAdapter(getActivity(),
							activityHistory);
					mListView.setAdapter(historyAdapter);
					historyAdapter.notifyDataSetChanged();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
				} else {
					historyAdapter = new HistoryAdapter(getActivity(),
							activityHistory);
					mListView.setAdapter(historyAdapter);
					historyAdapter.notifyDataSetChanged();
					mRefresh.setVisibility(View.GONE);
					mFLHistory.setVisibility(View.VISIBLE);
					mLLHishtoryView.setVisibility(View.INVISIBLE);
				}
			} catch (DbException e) {
				LogUtil.error("db", "数据库出现异常");
				e.printStackTrace();
			}
		} else {
			if (activityHistory != null) {
				historyAdapter = new HistoryAdapter(getActivity(),
						activityHistory);
				mListView.setAdapter(historyAdapter);
				historyAdapter.notifyDataSetChanged();
			}

			mLLHishtoryView.setVisibility(View.INVISIBLE);
			mRefresh.setVisibility(View.GONE);
			mFLHistory.setVisibility(View.VISIBLE);
			mLLHishtoryView.setVisibility(View.INVISIBLE);
			LogUtil.error("db", "=null");
		}

	}

	private void setOnViewClickListener() {

		mCTClearHistory.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @Title: justWishOrActivityOrComment
	 * @Description: 判断搜索类型
	 * @param
	 * @return
	 * @throws
	 */
	private void justWishOrActivityOrComment() {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("isActivity")) {
				searchActivityHistoryData();
			} else if (type.equals("isWish")) {
				searchWishHistoryData();
			} else if (type.equals("isComment")) {
				searchCommentHistoryData();
			} else {
				LogUtil.error("您传的类型错误", "type错误");
			}

		} else {
			LogUtil.error("您还没有传类型", "type=null");
		}

	}

	/**
	 * 
	 * @Title: justWishOrActivityOrComment
	 * @Description: 判断搜索类型
	 * @param
	 * @return
	 * @throws
	 */
	private void justWishheader() {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("isActivity")) {
				setHeadTitle("2");
			} else if (type.equals("isWish")) {
				setHeadTitle("0");
			} else if (type.equals("isComment")) {
				setHeadTitle("1");
			} else {
				LogUtil.error("您传的类型错误", "type错误");
				searchCommentHistoryData();
			}

		} else {
			searchCommentHistoryData();
			LogUtil.error("您还没有传类型", "type=null");
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.ct_clear_history:

			if (!TextUtils.isEmpty(type)) {
				if (type.equals("isActivity")) {
					clearActivityHistory();
				} else if (type.equals("isWish")) {
					clearWishHistory();
				} else if (type.equals("isComment")) {
					clearCommentHistory();
				} else {
					LogUtil.error("您传的类型错误", "type错误");
				}

			} else {
				LogUtil.error("您还没有传类型", "type=null");
			}

			break;

		default:
			break;
		}

	}

	/**
	 * @Title: clearWishHistory
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void clearWishHistory() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			// =db.deleteAll(Selector.from(HistoryBean.class));
			// listHistory = db.findAll(Selector
			// .from(HistoryBean.class));
			if (listHistory != null) {
				try {
					db.deleteAll(listHistory);
					listHistory = db.findAll(Selector.from(HistoryBean.class));
				} catch (DbException e) {
					e.printStackTrace();
					LogUtil.error("删除失败", "");
					return;
				}
				if (listHistory != null && listHistory.size() > 0) {
					mLLHishtoryView.setVisibility(View.VISIBLE);
				} else {
					historyAdapter.setData(listHistory);
					historyAdapter.notifyDataSetChanged();
					mLLHishtoryView.setVisibility(View.GONE);
				}

			} else {
				LogUtil.error("没有历史记录", "");
			}

		} else {

		}
	}

	/**
	 * @Title: clearCommentHistory
	 * @Description: 清楚历史记录
	 * @param
	 * @return
	 * @throws
	 */
	private void clearCommentHistory() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			// =db.deleteAll(Selector.from(HistoryBean.class));
			// listHistory = db.findAll(Selector
			// .from(HistoryBean.class));
			if (commentHistoryList != null) {
				try {
					db.deleteAll(commentHistoryList);
					commentHistoryList = db.findAll(Selector
							.from(CommentHistoryBean.class));
				} catch (DbException e) {
					e.printStackTrace();
					LogUtil.error("删除失败", "");
					return;
				}
				if (commentHistoryList != null && commentHistoryList.size() > 0) {
					mLLHishtoryView.setVisibility(View.VISIBLE);
				} else {
					historyAdapter.setData(commentHistoryList);
					historyAdapter.notifyDataSetChanged();
					mLLHishtoryView.setVisibility(View.GONE);
				}

			} else {
				LogUtil.error("没有历史记录", "");
			}

		} else {

		}
	}

	private void clearActivityHistory() {
		DbUtils db = DbUtils.create(getActivity());
		if (db != null) {
			// =db.deleteAll(Selector.from(HistoryBean.class));
			// listHistory = db.findAll(Selector
			// .from(HistoryBean.class));
			if (activityHistory != null) {
				try {
					db.deleteAll(activityHistory);
					activityHistory = db.findAll(Selector
							.from(ActivityHistoryBean.class));
				} catch (DbException e) {
					e.printStackTrace();
					LogUtil.error("删除失败", "");
					return;
				}
				if (activityHistory != null && activityHistory.size() > 0) {
					mLLHishtoryView.setVisibility(View.VISIBLE);
				} else {
					historyAdapter.setData(activityHistory);
					historyAdapter.notifyDataSetChanged();
					mLLHishtoryView.setVisibility(View.INVISIBLE);
				}

			} else {
				LogUtil.error("没有历史记录", "");
			}

		} else {

		}
	}

	/**
	 * @Title: searchHistory
	 * @Description: 搜索活动历史记录
	 * @param
	 * @return
	 * @throws
	 */
	private void searchActHistory(String strName, boolean isHot) {
		if (!TextUtils.isEmpty(strName)) {
			if (isHot) {

			} else {
				DbUtils dbUtils = DbUtils.create(getActivity());

				ActivityHistoryBean history = new ActivityHistoryBean();
				history.setHistoryName(strName);
				try {
					ActivityHistoryBean searHistoryBean = dbUtils
							.findFirst(Selector.from(ActivityHistoryBean.class)
									.where("historyName", "=", strName));
					if (searHistoryBean != null) {
						dbUtils.delete(searHistoryBean);
						dbUtils.save(history);
					} else {
						dbUtils.save(history);
					}

				} catch (DbException e) {
					e.printStackTrace();
				}
			}

			if (onSearchListener != null) {
				onSearchListener.OnSearchListener(strName);
			}
			if (mFLHistory != null)
				mFLHistory.setVisibility(View.GONE);

		} else {
			ComponentUtil.showToast(getActivity(), "请输入搜索内容");
		}
	}

	private void searchHistory(String strName, boolean isHot) {
		if (!TextUtils.isEmpty(strName)) {
			if (isHot) {
				// 不做任何处理
			} else {
				DbUtils dbUtils = DbUtils.create(getActivity());
				HistoryBean history = new HistoryBean();
				history.setHistoryName(strName);
				try {
					HistoryBean searHistoryBean = dbUtils.findFirst(Selector
							.from(HistoryBean.class).where("historyName", "=",
									strName));
					if (searHistoryBean != null) {
						dbUtils.delete(searHistoryBean);
						dbUtils.save(history);
					} else {
						dbUtils.save(history);
					}

				} catch (DbException e) {
					e.printStackTrace();
				}
			}

			if (onSearchListener != null) {
				onSearchListener.OnSearchListener(strName);
			}
			if (mFLHistory != null)
				mFLHistory.setVisibility(View.GONE);

		} else {
			ComponentUtil.showToast(getActivity(), "请输入搜索内容");
		}
	}

	/**
	 * 
	 * @Title: searchCommentHistory
	 * @Description: 搜索点评历史记录
	 * @param
	 * @return
	 * @throws
	 */
	private void searchCommentHistory(String strName, boolean isHot) {
		if (!TextUtils.isEmpty(strName)) {
			if (isHot) {

			} else {
				DbUtils dbUtils = DbUtils.create(getActivity());

				CommentHistoryBean history = new CommentHistoryBean();
				history.setHistoryName(strName);
				try {
					CommentHistoryBean searHistoryBean = dbUtils
							.findFirst(Selector.from(CommentHistoryBean.class)
									.where("historyName", "=", strName));
					if (searHistoryBean != null) {
						dbUtils.delete(searHistoryBean);
						dbUtils.save(history);
					} else {
						dbUtils.save(history);
					}

				} catch (DbException e) {
					e.printStackTrace();
				}
			}

			if (onSearchListener != null) {
				onSearchListener.OnSearchListener(strName);
			}
			if (mFLHistory != null)
				mFLHistory.setVisibility(View.GONE);

		} else {
			ComponentUtil.showToast(getActivity(), "请输入搜索内容");
		}
	}

	/**
	 * 
	 * @ClassName: OnSearchListener
	 * @Description: 搜索监听
	 * @author wuyulong
	 * @date 2015-4-11 上午10:45:37
	 * 
	 */
	public interface OnSearchListener {
		public void OnSearchListener(String name);

	}

	public void setOnSearchListener(OnSearchListener onSearchListener) {
		this.onSearchListener = onSearchListener;
	}

	private OnSearchListener onSearchListener;
	private boolean noHeader;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != -1 && position > 0) {
			if (noHeader) {
				onItemSelectPosition(position);
			} else {
				onItemSelectPosition(position - 1);
			}

		} else {
			noHeader = true;
			onItemSelectPosition(position);
		}

	}

	/**
	 * @Title: onItemSelectPosition
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void onItemSelectPosition(int position) {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("isActivity")) {
				if (activityHistory != null && activityHistory.size() > 0) {
					HistoryBean item = activityHistory.get(position);
					String storyName = item.getHistoryName();
					if (!TextUtils.isEmpty(storyName)) {
						searchActHistory(storyName, false);
					}

				}
			} else if (type.equals("isWish")) {
				if (listHistory != null && listHistory.size() > 0) {
					HistoryBean item = listHistory.get(position);
					String storyName = item.getHistoryName();
					if (!TextUtils.isEmpty(storyName)) {
						searchHistory(storyName, false);
					}

				}
			} else if (type.equals("isComment")) {

				HistoryBean item = commentHistoryList.get(position);
				String storyName = item.getHistoryName();
				if (!TextUtils.isEmpty(storyName)) {
					searchCommentHistory(storyName, false);
				}

			} else {
				LogUtil.error("您传的类型错误", "type错误");
			}

		} else {
			LogUtil.error("您还没有传类型", "type=null");
		}
	}

}
