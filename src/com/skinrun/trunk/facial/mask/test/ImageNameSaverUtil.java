package com.skinrun.trunk.facial.mask.test;

import java.util.List;

import com.app.base.entity.ProductImageEntity;
import com.base.app.utils.DBService;

public class ImageNameSaverUtil {
	//保存
	public static void save(ProductImageEntity imageEntity){
		List<ProductImageEntity> imageEntitys=DBService.getDB().findAllByWhere(ProductImageEntity.class, "productId='"+imageEntity.getProductId()+"'");
		if(imageEntitys!=null&&imageEntitys.size()>0){
			DBService.getDB().deleteByWhere(ProductImageEntity.class, "productId='"+imageEntity.getProductId()+"'");
		}
		DBService.getDB().save(imageEntity);
	}
	//查找
	public static String getImageName(String productId){
		List<ProductImageEntity> imageEntitys=DBService.getDB().findAllByWhere(ProductImageEntity.class, "productId='"+productId+"'");
		if(imageEntitys!=null&&imageEntitys.size()>0){
			return imageEntitys.get(0).getImageName();
		}
		return null;
	}
}
