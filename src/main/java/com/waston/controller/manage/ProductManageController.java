package com.waston.controller.manage;

import com.waston.common.Consts;
import com.waston.common.ResponseCode;
import com.waston.common.ServerResponse;
import com.waston.pojo.Product;
import com.waston.pojo.User;
import com.waston.service.FileService;
import com.waston.service.ProductService;
import com.waston.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author wangtao
 * @Date 2018/1/19
 **/
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    /**
     * 添加商品或者修改商品接口
     * @param product
     * @param session
     * @return
     */
    @RequestMapping(value = "/save.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse productSaveOrUpdate(Product product, HttpSession session){
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        return productService.saveOrUpdateProduct(product);

    }

    /**
     * 修改商品状态信息, 上下架
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "/set_sale_status.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse setProductStatus(Integer productId, Integer status, HttpSession session) {
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        return productService.updateProductStatus(productId, status);
    }

    /**
     * 商品详情接口
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "/detail.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse getDetail(Integer productId, HttpSession session){
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        return productService.getDetail(productId);
    }

    /**
     * 分页查询接口
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "/list.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse listProduct(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize, HttpSession session){
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        return productService.getList(pageNum, pageSize);
    }

    /**
     * 商品搜索接口
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/search.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        return productService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 图片上传接口
     * @param multipartFile
     * @param session
     * @return
     */
    @RequestMapping(value = "/upload.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public ServerResponse uploadFile(@RequestParam(value = "upload_file",required = false)MultipartFile multipartFile, HttpSession session) {
        ServerResponse<String> response = check(session);
        if(response != null)
            return response;
        String path = session.getServletContext().getRealPath("/upload");
        String fileName = fileService.uploadFile(multipartFile, path);
        if(StringUtils.isEmpty(fileName))
            return ServerResponse.createByError("请选择一个图片文件");
        if(Objects.equals(fileName, "null"))
            return ServerResponse.createByError("文件上传失败");
        String host = PropertiesUtil.getProperty("ftp.server.http.prefix");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uri", fileName);
        map.put("url", host + fileName);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 富文本图片上传接口, 返回指定的json格式
     * @param multipartFile
     * @param session
     * @return
     */
    @RequestMapping(value = "/richtext_img_upload.do", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Map<String, Object> uploadFileByRichtext(@RequestParam(value = "upload_file",required = false)MultipartFile multipartFile, HttpSession session) {
        Map<String, Object> map = new LinkedHashMap<>();
        if(multipartFile == null) {
            map.put("success", false);
            map.put("msg", "请选择文件");
            map.put("file_path", "");
            return map;
        }
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null) {
            map.put("success", false);
            map.put("msg", "还未登录");
            map.put("file_path", multipartFile.getOriginalFilename());
            return map;
        }
        if(currentUser.getRole() != Consts.ADMIN_ROLE) {
            map.put("success", false);
            map.put("msg", "权限不够");
            map.put("file_path", multipartFile.getOriginalFilename());
            return map;
        }
        String path = session.getServletContext().getRealPath("/upload");
        String fileName = fileService.uploadFile(multipartFile, path);
        //上传失败
        if(fileName == null || Objects.equals("null", fileName)) {
            map.put("success", false);
            map.put("msg", "文件上传失败");
            map.put("file_path", multipartFile.getOriginalFilename());
            return map;
        }
        String host = PropertiesUtil.getProperty("ftp.server.http.prefix");
        map.put("success", true);
        map.put("msg", "文件上传成功");
        map.put("file_path", host + fileName);
        return map;
    }

    /**
     * 检查权限
     * @param session
     * @return
     */
    private ServerResponse<String> check(HttpSession session) {
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getStatus(),"还未登录");
        }

        if(currentUser.getRole() != Consts.ADMIN_ROLE) {
            return ServerResponse.createByError("无权限操作!");
        }
        return null;
    }


}
