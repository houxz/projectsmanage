package com.emg.poiwebeditor.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class RedisUtils {

	/****************************************************************
	 * 函数名称： GetScaleInfo 功能描述： 函数实现 通过比例尺获取 比例尺相关信息 参数描述： scale:比例尺 GridNum:比例尺网格数目
	 * strScale:比例尺字符 返回类型： 默认值
	 * ------------------------------------------------------------- 修改日期 |版本号 |修改人
	 * |修改内容 -------------------------------------------------------------
	 * 2009-03-08 00:00:00 V1.0 HappyBoy 创建函数
	 *****************************************************************/
	private static String getStrScale(int scale) {
		String strScale = new String();
		switch (scale) {
		case 1000000:
			strScale = "A";
			break;
		case 500000:
			strScale = "B";
			break;
		case 250000:
			strScale = "C";
			break;
		case 100000:
			strScale = "D";
			break;
		case 50000:
			strScale = "E";
			break;
		case 25000:
			strScale = "F";
			break;
		case 10000:
			strScale = "G";
			break;
		case 5000:
			strScale = "H";
			break;
		case 2500:
			strScale = "I";
			break;
		case 1000:
			strScale = "J";
			break;
		default:
			return new String();
		}
		return strScale;
	}

	/****************************************************************
	 * 函数名称： GetScaleInfo 功能描述： 函数实现 通过比例尺获取 比例尺相关信息 参数描述： scale:比例尺 GridNum:比例尺网格数目
	 * strScale:比例尺字符 返回类型： 默认值
	 * ------------------------------------------------------------- 修改日期 |版本号 |修改人
	 * |修改内容 -------------------------------------------------------------
	 * 2009-03-08 00:00:00 V1.0 HappyBoy 创建函数
	 *****************************************************************/
	private static Integer getGridNum(int scale) {
		Integer GridNum = 0;
		switch (scale) {
		case 1000000:
			GridNum = 1;
			break;
		case 500000:
			GridNum = 2;
			break;
		case 250000:
			GridNum = 4;
			break;
		case 100000:
			GridNum = 12;
			break;
		case 50000:
			GridNum = 24;
			break;
		case 25000:
			GridNum = 48;
			break;
		case 10000:
			GridNum = 96;
			break;
		case 5000:
			GridNum = 192;
			break;
		case 2500:
			GridNum = 384;
			break;
		case 1000:
			GridNum = 768;
			break;
		default:
			return 0;
		}
		return GridNum;
	}
	
	/****************************************************************
	*  函数名称： GetMeshIDByCoord
	*  功能描述： 函数实现 通过点经纬度求 某个比例尺下 的 图幅编号
	*  参数描述： xcoord: 经度  ycoord:纬度  scale:比例尺
	*  返回类型： 图幅编号
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static String GetMeshIDByCoord(double xcoord, double ycoord, int scale)
	{
		int GridNum = getGridNum(scale);
		String strScale = getStrScale(scale);

		//计算比例尺的经差、纬差
	    double dScaleLat = 4.0 / GridNum;
	    double dScaleLon = 6.0 / GridNum;

		//计算1000,000比例尺中经度、纬度带编码
	    int ILat = (int) (ycoord / 4) + 1;
	    int ILon = (int) (xcoord / 6) + 31;
	    
		//计算某比例尺在1000000比例尺中行、列号
		int IRow  = GridNum - (int)((ycoord - (int)(ycoord / 4) * 4) / dScaleLat);
	    int ILine = (int)((xcoord - ((int)(xcoord / 6) * 6)) / dScaleLon) + 1;

	    String CharLat = GetILatChar(ILat);
		String strMeshID = String.format("%s%02d%s%03d%03d", CharLat, ILon, strScale, IRow, ILine);
		return strMeshID;

	}
	
	/****************************************************************
	*  函数名称： GetILatInt
	*  功能描述： 函数实现 纬度字符串 转 纬度数值
	*  参数描述： strLat：纬度字符串
	*  返回类型： 纬度数值
	*  -------------------------------------------------------------
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static int GetILatInt(String strLat)
	{
		if (strLat.equals("A")) return 1;
		else if (strLat.equals("B")) return 2;
		else if (strLat.equals("C")) return 3;
		else if (strLat.equals("D")) return 4;
		else if (strLat.equals("E")) return 5;
		else if (strLat.equals("F")) return 6;
		else if (strLat.equals("G")) return 7;
		else if (strLat.equals("H")) return 8;
		else if (strLat.equals("I")) return 9;
		else if (strLat.equals("J")) return 10;
		else if (strLat.equals("K")) return 11;
		else if (strLat.equals("L")) return 12;
		else if (strLat.equals("M")) return 13;
		else if (strLat.equals("N")) return 14;
		else if (strLat.equals("O")) return 15;
		else if (strLat.equals("P")) return 16;
		else if (strLat.equals("Q")) return 17;
		else if (strLat.equals("R")) return 18;
		else if (strLat.equals("S")) return 19;
		else if (strLat.equals("T")) return 20;
		else if (strLat.equals("U")) return 21;
		else if (strLat.equals("V")) return 22;
		else return 1;
	}
	
	/****************************************************************
	*  函数名称： GetScaleInt
	*  功能描述： 函数实现 比例尺字符转 比例尺数值
	*  参数描述： strScale：比例尺字符
	*  返回类型： 比例尺数值
	*  -------------------------------------------------------------
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static int GetScaleInt(String strScale)
	{
		if (strScale.equals("A")) return 1;
		else if (strScale.equals("B")) return 2;
		else if (strScale.equals("C")) return 4;
		else if (strScale.equals("D")) return 12;
		else if (strScale.equals("E")) return 24;
		else if (strScale.equals("F")) return 48;
		else if (strScale.equals("G")) return 96;
		else if (strScale.equals("H")) return 192;
		else if (strScale.equals("I")) return 384;
		else if (strScale.equals("J")) return 768;
		else return 1;
	}
	
	/****************************************************************
	*  函数名称： GetILatChar
	*  功能描述： 函数实现 纬度数值 转 纬度字符串
	*  参数描述： ILat：纬度数值
	*  返回类型： 纬度字符串
	*  -------------------------------------------------------------
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static String GetILatChar(int ILat)
	{
		String strLat = new String();
		switch(ILat)
		{
		case 1:		strLat = "A"; break;
		case 2:		strLat = "B"; break;
		case 3:		strLat = "C"; break;
		case 4:		strLat = "D"; break;
		case 5:		strLat = "E"; break;
		case 6:		strLat = "F"; break;
		case 7:		strLat = "G"; break;
		case 8:		strLat = "H"; break;
		case 9:		strLat = "I"; break;
		case 10:	strLat = "J"; break;
		case 11:	strLat = "K"; break;
		case 12:	strLat = "L"; break;
		case 13:	strLat = "M"; break;
		case 14:	strLat = "N"; break;
		case 15:	strLat = "O"; break;
		case 16:	strLat = "P"; break;
		case 17:	strLat = "Q"; break;
		case 18:	strLat = "R"; break;
		case 19:	strLat = "S"; break;
		case 20:	strLat = "T"; break;
		case 21:	strLat = "U"; break;
		case 22:	strLat = "V"; break;
		default:	strLat = "A"; break;
		}
		return strLat;
	}
	
	private static int getILat(String strMeshID) { return GetILatInt(strMeshID.substring(0,1)); }
	private static int getILon(String strMeshID) { return Integer.valueOf(strMeshID.substring(1,3)); }
	private static int getGridNum(String strMeshID) {return GetScaleInt(strMeshID.substring(3,4)); }
	private static int getIRow(String strMeshID) {return Integer.valueOf(strMeshID.substring(4,7)); }
	private static int getILine(String strMeshID) {return Integer.valueOf(strMeshID.substring(7,10)); }
	
	/****************************************************************
	*  函数名称： GetMeshIDByMeshInfo
	*  功能描述： 函数实现 网格数字编号转为网格字符串编号
	*  参数描述： 网格数字编号信息
	*  返回类型： 网格字符串编号 <==> J50A001001 网格字符串编号
	*  -------------------------------------------------------------
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static String GetMeshIDByMeshInfo(int ILat, int ILon, String strScale, int IRow, int ILine)
	{
		String CharLat = GetILatChar(ILat);
		String strMeshID = CharLat + String.format("%02d", ILon) + strScale + String.format("%03d", IRow) + String.format("%03d", ILine);
		return strMeshID;
	}

	private static List<String> GetVMeshIDByRect(double x_min, double y_min, double x_max, double y_max, int scale) {
		List<String> VMeshIDs = new ArrayList<String>();
		int GridNum = getGridNum(scale);
		String strScale = getStrScale(scale);
		int GridNumLB = 0, GridNumLT = 0, GridNumRB = 0, GridNumRT = 0;
		int ILatLB = 0, ILatLT = 0, ILatRB = 0, ILatRT = 0;
		int ILonLB = 0, ILonLT = 0, ILonRB = 0, ILonRT = 0;
		int IRowLB = 0, IRowLT = 0, IRowRB = 0, IRowRT = 0;
		int ILineLB = 0, ILineLT = 0, ILineRB = 0, ILineRT = 0;
		
		String strMeshIDLB = GetMeshIDByCoord(x_min, y_min, scale);
		String strMeshIDRT = GetMeshIDByCoord(x_max, y_max, scale);
		String strMeshIDLT = GetMeshIDByCoord(x_min, y_max, scale);
		String strMeshIDRB = GetMeshIDByCoord(x_max, y_min, scale);
		
		ILatLB = getILat(strMeshIDLB);
		ILonLB = getILon(strMeshIDLB);
		GridNumLB = getGridNum(strMeshIDLB);
		IRowLB = getIRow(strMeshIDLB);
		ILineLB = getILine(strMeshIDLB);
		
		ILatLT = getILat(strMeshIDLT);
		ILonLT = getILon(strMeshIDLT);
		GridNumLT = getGridNum(strMeshIDLT);
		IRowLT = getIRow(strMeshIDLT);
		ILineLT = getILine(strMeshIDLT);
		
		ILatRB = getILat(strMeshIDRB);
		ILonRB = getILon(strMeshIDRB);
		GridNumRB = getGridNum(strMeshIDRB);
		IRowRB = getIRow(strMeshIDRB);
		ILineRB = getILine(strMeshIDRB);
		
		ILatRT = getILat(strMeshIDRT);
		ILonRT = getILon(strMeshIDRT);
		GridNumRT = getGridNum(strMeshIDRT);
		IRowRT = getIRow(strMeshIDRT);
		ILineRT = getILine(strMeshIDRT);
		
		String strMeshID = "";
		int rows = 0, rowe = 0;
		int lines = 0, linee = 0;
		boolean flagLat = false, flagLon = false;
		
		for (int indexlat = ILatLB; indexlat <= ILatRT; indexlat++) {
			if (ILatLB == ILatRT)
			{
				rows = IRowRT;
				rowe = IRowLB;
				flagLat = true;
			}
			else
			{
				if (indexlat == ILatLB)
				{
					rows = 1;
					rowe = IRowLB;
				}
				else if (indexlat == ILatRT)
				{
					rows = IRowRT;
					rowe = GridNumLB;
				}
				else
				{
					rows = 1;
					rowe = GridNumLB;
				}
			}
			for (int indexlon = ILonLB; indexlon <= ILonRT; indexlon++)
			{
				if (ILonLB == ILonRT)
				{
					lines = ILineLB;
					linee = ILineRT;
					flagLon = true;
				}
				else
				{
					if (indexlon == ILonLB)
					{
						lines = ILineLB;
						linee = GridNumLB;
					}
					else if (indexlon == ILonRT)
					{
						lines = 1;
						linee = ILineRT;
					}
					else
					{
						lines = 1;
						linee = GridNumLB;
					}
				}

				for (int indexrow = rows; indexrow <= rowe; indexrow++)
				{
					for (int indexline = lines; indexline <= linee; indexline++)
					{
						strMeshID = GetMeshIDByMeshInfo(indexlat,indexlon,strScale,indexrow,indexline);
						VMeshIDs.add(strMeshID);
					}
				}
				if (flagLon)
				{
					break;
				}
			}
			if (flagLat)
			{
				break;
			}
		}
		return VMeshIDs;
	}
	
	/****************************************************************
	*  函数名称： GetRectByMeshID
	*  功能描述： 函数实现 通过图幅编号 计算 图幅的矩形框
	*  参数描述： strMeshID: 图幅编号
	*  返回类型： 图幅边界矩形框
	*  -------------------------------------------------------------
	*  修改日期             |版本号  |修改人   |修改内容    
	*  -------------------------------------------------------------
	*  2009-03-08 00:00:00   V1.0     HappyBoy  创建函数
	*****************************************************************/
	private static Map<String, String> GetRectByMeshID(String strMeshID, int scale)
	{
		Map<String, String> geoMeshBound = new HashMap<String, String>();
		if (strMeshID.length() != 10)
			return geoMeshBound;
		
		//J50A001001 字符串转为网格数字编号
		int ILat = 0, ILon = 0, GridNum = 0, IRow = 0, ILine = 0;
		ILat = getILat(strMeshID);
		ILon = getILon(strMeshID);
		GridNum = getGridNum(strMeshID);
		IRow = getIRow(strMeshID);
		ILine = getILine(strMeshID);
		//计算比例尺的经差、纬差
	    double dScaleLat = 4.0 / GridNum;
	    double dScaleLon = 6.0 / GridNum;

		//求西南角定点坐标
		double XL = (ILon - 31) * 6 + (ILine - 1) * dScaleLon;
	    double YB = (ILat - 1) * 4 + (GridNum - IRow) * dScaleLat;
		double XR = (ILon - 31) * 6 + (ILine) * dScaleLon;
	    double YT = (ILat - 1) * 4 + (GridNum + 1 - IRow) * dScaleLat;
		
	    geoMeshBound.put("x_max", String.format("%.6f", XR));
	    geoMeshBound.put("x_min", String.format("%.6f", XL));
	    geoMeshBound.put("y_max", String.format("%.6f", YT));
	    geoMeshBound.put("y_min", String.format("%.6f", YB));
		
	    
	    return geoMeshBound;
	}
	
	/**
	 * 基于提供的位置信息列表，计算出与之交替的bounds
	 * 
	 * @param geos
	 * @return
	 */
	public static Set<String> getBoundsByGeometry(List<Geometry> geos) {
		Set<String> bounds = new HashSet<String>();
		for (Geometry geo : geos) {
	    	Coordinate[] coordinates = geo.getCoordinates();
	    	for(int i = 0; i < coordinates.length - 1; i++) {
	    		Coordinate x = coordinates[i];
	    		Coordinate y = coordinates[i+1];
	    		
	    		List<String> VMeshIDs = GetVMeshIDByRect((x.x < y.x ? x.x : y.x), (x.y < y.y ? x.y : y.y), (x.x > y.x ? x.x : y.x), (x.y > y.y ? x.y : y.y), 2500);
	    		for(String VMeshID : VMeshIDs) {
	    			Map<String, String> map = GetRectByMeshID(VMeshID, 2500);
	    			
	    			bounds.add(String.format("%s,%s,%s,%s", map.get("x_min"), map.get("y_min"), map.get("x_max"), map.get("y_max")));
	    		}
	    	}
	    }
		return bounds;
	}

}
