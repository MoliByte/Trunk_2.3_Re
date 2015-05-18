package com.skinrun.trunk.facial.mask.test;

import java.util.List;

import com.app.base.entity.ProductEntity;
import com.base.app.utils.DBService;

public class SaveSelectedProductUtil {
	public static void save(ProductEntity product){
		List<ProductEntity> products=DBService.getDB().findAllByWhere(ProductEntity.class, "token='"+product.getToken()+"'");
		if(products!=null&&products.size()>0){
			for(int i=0;i<products.size();i++){
				DBService.getDB().delete(products.get(i));
			}
		}
		
		DBService.getDB().save(product);
	}
	public static ProductEntity getSelectProduct(String token){
		List<ProductEntity> products=DBService.getDB().findAllByWhere(ProductEntity.class, "token='"+token+"'");
		if(products!=null&&products.size()>0){
			return products.get(products.size()-1);
		}
		return null;
	}
	
	//删除记录
	public static void delete(String token){
		DBService.getDB().deleteByWhere(ProductEntity.class, "token='"+token+"'");
	}
}
