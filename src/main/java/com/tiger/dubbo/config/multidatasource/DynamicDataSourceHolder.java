package com.tiger.dubbo.config.multidatasource;

import com.tiger.dubbo.utils.Constants;

public class DynamicDataSourceHolder {
	
    //使用ThreadLocal记录当前线程的数据源name
	public static final ThreadLocal<String> holder = new ThreadLocal<String>();

	/** 
     * 功能：设置数据源name 
     * @param name 
     */  
	public static void putDataSource(String name) {
		holder.set(name);
	}

	/** 
     * 功能：获取数据源name 
     * @return 
     */ 
	public static String getDataSouce() {
		return holder.get();
	}
	
	/** 
     * 功能：设置主机模式
     */  
    public static void putMasterDataSource(){  
        putDataSource(Constants.MASTER_DATASOURCE);
    }  
      
    /** 
     * 功能：设置从机模式
     */  
    public static void putSlaveDataSource(){  
        putDataSource(Constants.SLAVE_DATASOURCE_CODE);
    }  
}
