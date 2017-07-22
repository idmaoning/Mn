package com.mn.comm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liming.lu on 2016/6/7.
 */
public class PageList<T> implements Serializable {

	private static final long serialVersionUID = -7994135814503213171L;

	@SuppressWarnings("rawtypes")
	private List<T> page = null; // 记录列表

	private int pageCount = 0; // 总页数

	private long recordCount = 0; // 总记录数目

	private int pageIndex = 0; // 第几页

	private int pageSize = 0; // 分页大小

	public void setPage(List<T> page) {
		this.page = page;
	}

	/**
	 * 根据分页索引、分页大小、分页总数、记录总数将已经分割的列表组成分页列表
	 *
	 * @param page        已经分割的列表
	 * @param pageCount   分页总数
	 * @param recordCount 记录总数
	 * @param pageIndex   分页索引
	 * @param pageSize    分页大小
	 */
	@SuppressWarnings("rawtypes")
	public PageList(List<T> page, int pageCount, long recordCount, int pageIndex, int pageSize) {
		this.makePageList(page, pageCount, recordCount, pageIndex, pageSize);
	}

	public PageList() {
	}

	public PageList(List<T> page, long recordCount, int pageIndex, int pageSize) {

		if (recordCount == 0) {
			this.makePageList(new ArrayList(), 0, 0, 0, pageSize);
		} else {
			if (pageSize <= 0) {
				pageSize = 10;
			}
			Long pageCount1 = recordCount / pageSize;

			int pageCount = pageCount1.intValue();
			if (recordCount % pageSize != 0) {
				pageCount++;
			}
			pageIndex = pageIndex < 1 ? 1 : pageIndex;
			pageIndex = pageIndex > pageCount ? pageCount : pageIndex;

			int skip = pageSize * (pageIndex - 1);

			int fromIndex = (skip <= 0) ? 0 : skip;// - 1;
//			int count = ((recordCount - skip) / pageSize == 0) ? (recordCount - skip) : pageSize;
//
//			List realList = list.subList(fromIndex, fromIndex + count);

			this.makePageList(page, pageCount, recordCount, pageIndex, pageSize);

		}
	}

	/**
	 * 根据分页索引、分页大小自动将完整列表分割成分页列表
	 *
	 * @param list      完整列表
	 * @param pageIndex 分页索引
	 * @param pageSize  分页大小
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PageList(List<T> list, int pageIndex, int pageSize) {
		int recordCount = list.size();
		if (recordCount == 0) {
			this.makePageList(new ArrayList(), 0, 0, 0, pageSize);
		} else {
			if (pageSize <= 0) {
				pageSize = 10;
			}
			int pageCount = recordCount / pageSize;
			if (recordCount % pageSize != 0) {
				pageCount++;
			}
			pageIndex = pageIndex < 1 ? 1 : pageIndex;
			pageIndex = pageIndex > pageCount ? pageCount : pageIndex;

			int skip = pageSize * (pageIndex - 1);

			int fromIndex = (skip <= 0) ? 0 : skip;// - 1;
			int count = ((recordCount - skip) / pageSize == 0) ? (recordCount - skip) : pageSize;

			List realList = list.subList(fromIndex, fromIndex + count);

			this.makePageList(realList, pageCount, recordCount, pageIndex, pageSize);
		}
	}

	public List<T> getPage() {
		return page;
	}

	public int getPageCount() {
		return pageCount;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	@SuppressWarnings("rawtypes")
	public void makePageList(List<T> page, int pageCount, long recordCount, int pageIndex, int pageSize) {
		if (page == null) {
			page = new ArrayList();
		}
		this.page = page;
		this.pageCount = pageCount;
		this.recordCount = recordCount;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;

	}

	public int getPrevPageIndex() {
		int idx = pageIndex - 1;
		if (idx < 0) {
			idx = 0;
		}
		return idx;
	}

	public int getNextPageIndex() {
		int idx = pageIndex + 1;
		if (idx >= pageCount) {
			idx = pageCount - 1;
		}
		if (idx < 0) {
			idx = 0;
		}
		return idx;
	}

	public boolean hasNext() {
		return pageIndex + 1 < pageCount;
	}

	public boolean hasPrevious() {
		return pageIndex > 0;
	}

	public boolean hasPage() {
		if (null == page || page.size() <= 0) {
			return false;
		}
		return true;
	}

	public boolean isLast() {
		return !hasNext();
	}

	public boolean isFirst() {
		return !hasPrevious();
	}

}
