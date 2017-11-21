/**
 * 任务优先级变更
 */
function sortable(){
//		$("#jobsinfos tbody").sortable({                //这里是talbe tbody，绑定 了sortable   
//            helper: fixHelper,                  //调用fixHelper   
//            axis:"y",  
//            start:function(e, ui){  
//                ui.helper.css({"background":"#fff"});    //拖动时的行，要用ui.helper   
//                 return ui;  
//             },  
//             stop:function(e, ui){  
//            	 var $tr = $(ui.item[0]);
//            	 var id = $tr.attr("data-uniqueid");
//            	 if(id != null){
//            		 var pre = $tr.prev();
//            		 pre = pre.length == 0?0:pre.attr("data-uniqueid");
//            		 
//            		 var next = $tr.next();
//            		 next = next.length == 0?0:next.attr("data-uniqueid");
//            		 changePrio(id,pre,next);
//            	 }
//                 return ui;  
//            },
//            scroll: true
//         }).disableSelection();
}
// Return a helper with preserved width of cells   
var fixHelper = function(e, ui) {
     //console.log(ui) 
     ui.children().each(function() {
    	 $(this).width($(this).width());     //在拖动时，拖动行的cell（单元格）宽度会发生改变。在这里做了处理就没问题了   
     });
         return ui;
 };