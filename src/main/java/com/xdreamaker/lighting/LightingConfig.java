package com.xdreamaker.lighting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.abigdreamer.ark.data.excel.ExcelReader;
import com.abigdreamer.ark.data.excel.factory.Excel2007Factory;
import com.google.common.base.Joiner;

/**
 * @author Darkness
 * @date 2017年5月27日 下午4:27:16
 * @version 1.0
 * @since 1.0 
 */
public class LightingConfig {
	
	private static Map<String, Integer> mapping = new HashMap<>();
	
	public static void readExcel() {
		String filePath = System.getProperty("user.dir") + File.separator + "CargoNoModbusMapping.xlsx";
		File file = new File(filePath);
		ExcelReader readExcel = new Excel2007Factory().createExcelReader(file);
		try {
			readExcel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readExcel.setSheetNum(0); // 设置读取索引为0的工作表
		// 总行数
		int count = readExcel.getRowCount();
		
		mapping.clear(); 
		
		for (int i = 0; i <= count; i++) {
			String[] rows = readExcel.readExcelLine(i);
			if(rows != null) {
				System.out.println("[" + i + "]" + Joiner.on("  ").join(rows));
				try {
					mapping.put(rows[0], Integer.parseInt(rows[2]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("配置信息错误：" + rows[0] + "," + rows[1] + "," + rows[2]);
					System.out.println("尝试强制转换");
					try {
						mapping.put(rows[0], (int)Double.parseDouble(rows[2]));
						System.out.println("强制转换成功");
					} catch (NumberFormatException e1) {
						//e1.printStackTrace();
						System.out.println("强制转换失败，请检查配置");	
					}
				}
			}
		}
	}
	
	public static Map<String, Integer> getLightingIds() {
		return mapping;
	}

	public static Integer getCargoNo(String lightingId) {
		return mapping.get(lightingId);
	}
}
