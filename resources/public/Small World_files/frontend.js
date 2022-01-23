// Compiled by ClojureScript 1.10.520 {}
goog.provide('smallworld.frontend');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('smallworld.current_user');
goog.require('clj_fuzzy.metrics');
goog.require('cljs.pprint');
goog.require('cljsjs.mapbox');
goog.require('goog.dom');
goog.require('goog.dom.classlist');
if((typeof smallworld !== 'undefined') && (typeof smallworld.frontend !== 'undefined') && (typeof smallworld.frontend.current_user !== 'undefined')){
} else {
smallworld.frontend.current_user = reagent.core.atom.call(null,new cljs.core.Keyword(null,"loading","loading",-737050189));
}
if((typeof smallworld !== 'undefined') && (typeof smallworld.frontend !== 'undefined') && (typeof smallworld.frontend.friends !== 'undefined')){
} else {
smallworld.frontend.friends = reagent.core.atom.call(null,new cljs.core.Keyword(null,"loading","loading",-737050189));
}
smallworld.frontend.fetch = (function smallworld$frontend$fetch(route,callback){
return window.fetch(route).then((function (p1__51989_SHARP_){
return p1__51989_SHARP_.json();
})).then((function (p1__51990_SHARP_){
return cljs.core.js__GT_clj.call(null,p1__51990_SHARP_,new cljs.core.Keyword(null,"keywordize-keys","keywordize-keys",1310784252),true);
})).then((function (result){
return callback.call(null,result);
}));
});
smallworld.frontend.fetch.call(null,"/friends",(function (p1__51991_SHARP_){
return cljs.core.reset_BANG_.call(null,smallworld.frontend.friends,p1__51991_SHARP_);
}));
smallworld.frontend.fetch.call(null,"/session",(function (p1__51992_SHARP_){
return cljs.core.reset_BANG_.call(null,smallworld.frontend.current_user,p1__51992_SHARP_);
}));
smallworld.frontend.animated_globe = (function smallworld$frontend$animated_globe(){
var handle_hover_51993 = (function (){
var elem = goog.dom.getElement("logo-animation");
var start_animation = ((function (elem){
return (function (){
return goog.dom.classlist.remove(elem,"no-animation");
});})(elem))
;
var stop_animation = ((function (elem,start_animation){
return (function (){
return goog.dom.classlist.add(elem,"no-animation");
});})(elem,start_animation))
;
var stop_after_iteration = ((function (elem,start_animation,stop_animation){
return (function (){
return elem.addEventListener("webkitAnimationIteration",stop_animation).call(null,elem.addEventListener("animationiteration",stop_animation),elem.addEventListener("MSAnimationIteration",stop_animation),elem.addEventListener("animationiteration",stop_animation));
});})(elem,start_animation,stop_animation))
;
elem.addEventListener("mouseover",start_animation);

return elem.addEventListener("mouseout",stop_after_iteration);
});
setTimeout(handle_hover_51993,(1000));

return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),"globe-loader fas fa-globe-americas"], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"i.fas.fa-plane","i.fas.fa-plane",-1403273783)], null)], null);
});
smallworld.frontend.simple_loading_animation = (function smallworld$frontend$simple_loading_animation(){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"svg.loader","svg.loader",-465993809),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"path","path",-188191168),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"fill","fill",883462889),"#fff",new cljs.core.Keyword(null,"d","d",1972142424),"M73,50c0-12.7-10.3-23-23-23S27,37.3,27,50 M30.9,50c0-10.5,8.5-19.1,19.1-19.1S69.1,39.5,69.1,50"], null)], null)], null);
});
smallworld.frontend.logout = (function smallworld$frontend$logout(){
return smallworld.frontend.fetch.call(null,"/logout",(function (){
return cljs.core.reset_BANG_.call(null,smallworld.frontend.current_user,cljs.core.PersistentArrayMap.EMPTY);
}));
});
smallworld.frontend.nav = (function smallworld$frontend$nav(){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.nav","div.nav",-1805454552),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#logo-animation.logo","div#logo-animation.logo",-1750389297),smallworld.frontend.animated_globe.call(null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.logo-text","div.logo-text",-158529735),"small world"], null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.fill-nav-space","span.fill-nav-space",666400556)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"href","href",-793805698),"#about"], null),"about"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.links-spacer","span.links-spacer",2025237200),"\u00B7"], null),(((new cljs.core.Keyword(null,"screen-name","screen-name",31129652).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,smallworld.frontend.current_user)) == null))?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a#login","a#login",-1592114088),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"href","href",-793805698),"/login"], null),"sign in"], null):new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"href","href",-793805698),"/",new cljs.core.Keyword(null,"on-click","on-click",1632826543),smallworld.frontend.logout], null),"log out ",new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"b","b",1482224470),"@",new cljs.core.Keyword(null,"screen-name","screen-name",31129652).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,smallworld.frontend.current_user))], null)], null))], null);
});
smallworld.frontend.music = (function smallworld$frontend$music(){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"iframe","iframe",884422026),new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"src","src",-1651076051),"https://open.spotify.com/embed/track/3fWTQXs897m4H1zsai8SOk?utm_source=generator&theme=0",new cljs.core.Keyword(null,"width","width",-384071477),"100%",new cljs.core.Keyword(null,"height","height",1025178622),"80",new cljs.core.Keyword(null,"frameBorder","frameBorder",-1546202685),"0",new cljs.core.Keyword(null,"allowFullScreen","allowFullScreen",475491825),"",new cljs.core.Keyword(null,"allow","allow",-1857325745),"autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"], null)], null);
});
smallworld.frontend.friend_row_headers = new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, ["","name","handle","location","coordinates","distance","profile_image_url_large"], null);
smallworld.frontend.location_name_similarity = (function smallworld$frontend$location_name_similarity(friend){
return clj_fuzzy.metrics.jaro_winkler.call(null,new cljs.core.Keyword(null,"location","location",1815599388).cljs$core$IFn$_invoke$arity$1(friend).toLowerCase(),new cljs.core.Keyword(null,"location","location",1815599388).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,smallworld.frontend.current_user)).toLowerCase());
});
smallworld.frontend.friend_row = (function smallworld$frontend$friend_row(i,friend){
var twitter_handle = new cljs.core.Keyword(null,"screen-name","screen-name",31129652).cljs$core$IFn$_invoke$arity$1(friend);
var twitter_link = ["http://twitter.com/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(twitter_handle)].join('');
return new cljs.core.PersistentVector(null, 9, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),i], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(friend)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"href","href",-793805698),twitter_link], null),["@",cljs.core.str.cljs$core$IFn$_invoke$arity$1(twitter_handle)].join('')], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.Keyword(null,"location","location",1815599388).cljs$core$IFn$_invoke$arity$1(friend)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668).cljs$core$IFn$_invoke$arity$1(friend))], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.Keyword(null,"distance","distance",-1671893894).cljs$core$IFn$_invoke$arity$1(friend)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.Keyword(null,"profile_image_url_large","profile_image_url_large",-567342015).cljs$core$IFn$_invoke$arity$1(friend)], null)], null);
});
smallworld.frontend.table_header = cljs.core.map_indexed.call(null,(function (i,header){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null),header], null);
}),smallworld.frontend.friend_row_headers);
smallworld.frontend.get_smallest_distance = (function smallworld$frontend$get_smallest_distance(friend){
var x = cljs.core.apply.call(null,cljs.core.min,cljs.core.remove.call(null,cljs.core.nil_QMARK_,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [(9999999999999999),cljs.core.get_in.call(null,friend,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),new cljs.core.Keyword(null,"name-name","name-name",-1767411827)], null)),cljs.core.get_in.call(null,friend,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),new cljs.core.Keyword(null,"name-main","name-main",1464990880)], null)),cljs.core.get_in.call(null,friend,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),new cljs.core.Keyword(null,"main-name","main-name",-2124294023)], null)),cljs.core.get_in.call(null,friend,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),new cljs.core.Keyword(null,"main-main","main-main",41668739)], null))], null)));
return x;
});
smallworld.frontend.closer_than = (function smallworld$frontend$closer_than(max_distance,dist_key){
return (function (friend){
var smallest_distance = cljs.core.get_in.call(null,friend,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),dist_key], null));
return (((smallest_distance < max_distance)) && ((!((smallest_distance == null)))));
});
});
smallworld.frontend.round_two_decimals = (function smallworld$frontend$round_two_decimals(p1__51994_SHARP_){
return cljs.pprint.cl_format.call(null,null,"~,2f",p1__51994_SHARP_);
});
smallworld.frontend.preify = (function smallworld$frontend$preify(obj){
var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__51995_51999 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__51996_52000 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__51997_52001 = true;
var _STAR_print_fn_STAR__temp_val__51998_52002 = ((function (_STAR_print_newline_STAR__orig_val__51995_51999,_STAR_print_fn_STAR__orig_val__51996_52000,_STAR_print_newline_STAR__temp_val__51997_52001,sb__4661__auto__){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__51995_51999,_STAR_print_fn_STAR__orig_val__51996_52000,_STAR_print_newline_STAR__temp_val__51997_52001,sb__4661__auto__))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__51997_52001;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__51998_52002;

try{cljs.pprint.pprint.call(null,obj);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__51996_52000;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__51995_51999;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
});
smallworld.frontend.Friend = (function smallworld$frontend$Friend(k,friend){
var twitter_pic = new cljs.core.Keyword(null,"profile_image_url_large","profile_image_url_large",-567342015).cljs$core$IFn$_invoke$arity$1(friend);
var twitter_name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(friend);
var twitter_handle = new cljs.core.Keyword(null,"screen-name","screen-name",31129652).cljs$core$IFn$_invoke$arity$1(friend);
var twitter_link = ["http://twitter.com/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(twitter_handle)].join('');
var location = new cljs.core.Keyword(null,"main-location","main-location",1133157975).cljs$core$IFn$_invoke$arity$1(friend);
var twitter_href = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"href","href",-793805698),twitter_link,new cljs.core.Keyword(null,"target","target",253001721),"_blank"], null);
var lat = smallworld.frontend.round_two_decimals.call(null,new cljs.core.Keyword(null,"lat","lat",-580793929).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"main-coords","main-coords",-110867548).cljs$core$IFn$_invoke$arity$1(friend)));
var lng = smallworld.frontend.round_two_decimals.call(null,new cljs.core.Keyword(null,"lng","lng",1667213918).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"main-coords","main-coords",-110867548).cljs$core$IFn$_invoke$arity$1(friend)));
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.friend","div.friend",322037341),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),twitter_name], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),twitter_href,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.twitter-pic","div.twitter-pic",-667013169),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"img","img",1442687358),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"src","src",-1651076051),twitter_pic,new cljs.core.Keyword(null,"key","key",-1516042587),k], null)], null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.right-section","div.right-section",-1411151166),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a.top","a.top",345934002),twitter_href,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.name","span.name",-230739013),twitter_name], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.handle","span.handle",1538838579),"@",twitter_handle], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.bottom","div.bottom",56172931),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"href","href",-793805698),["https://www.google.com/maps/search/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(lat),"%20",cljs.core.str.cljs$core$IFn$_invoke$arity$1(lng),"?hl=en&source=opensearch"].join(''),new cljs.core.Keyword(null,"target","target",253001721),"_blank"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.location","span.location",-1369691478),location], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.coordinates","span.coordinates",1077087472),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.coord","span.coord",-863086254),lat], null)," ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.coord","span.coord",-863086254),lng], null)], null)], null)], null)], null)], null);
});
smallworld.frontend.get_close_friends = (function smallworld$frontend$get_close_friends(distance_key,max_distance){
return cljs.core.filter.call(null,smallworld.frontend.closer_than.call(null,max_distance,distance_key),cljs.core.sort_by.call(null,(function (p1__52003_SHARP_){
return cljs.core.get_in.call(null,p1__52003_SHARP_,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"distance","distance",-1671893894),distance_key], null));
}),cljs.core.deref.call(null,smallworld.frontend.friends)));
});
smallworld.frontend.render_friends_list = (function smallworld$frontend$render_friends_list(friends_list_key,verb,location_name){
var friends_list = smallworld.frontend.get_close_friends.call(null,friends_list_key,(100));
var list_count = cljs.core.count.call(null,friends_list);
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"<>","<>",1280186386),new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p.location-info","p.location-info",-1243237027),list_count," friends say they ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"b","b",1482224470),verb], null)," near ",location_name,":"], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067)], null),(((list_count > (0)))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.friends","div.friends",1562235669),cljs.core.map_indexed.call(null,smallworld.frontend.Friend,friends_list)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.no-friends-found","div.no-friends-found",522957404),"no friends found in this location"], null))], null);
});
smallworld.frontend.mapbox_config = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"frank-lloyd-wright","frank-lloyd-wright",1188392730),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"access-token","access-token",-654201199),"pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA",new cljs.core.Keyword(null,"style","style",-496642736),"mapbox://styles/devonzuegel/ckyn7uof70x1e14ppotxarzhc"], null),new cljs.core.Keyword(null,"minimo","minimo",-369687509),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"access-token","access-token",-654201199),"pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA",new cljs.core.Keyword(null,"style","style",-496642736),"mapbox://styles/devonzuegel/ckyootmv72ci414ppwl6j34a2"], null),new cljs.core.Keyword(null,"curios-bright","curios-bright",-1722814852),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"access-token","access-token",-654201199),"pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA",new cljs.core.Keyword(null,"style","style",-496642736),"mapbox://styles/devonzuegel/cj8rx2ti3aw2z2rnzhwwy3bvp"], null)], null);
smallworld.frontend.mapbox_style = new cljs.core.Keyword(null,"curios-bright","curios-bright",-1722814852);
mapboxgl.accessToken = cljs.core.get_in.call(null,smallworld.frontend.mapbox_config,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [smallworld.frontend.mapbox_style,new cljs.core.Keyword(null,"access-token","access-token",-654201199)], null));
smallworld.frontend.RenderMap = (function smallworld$frontend$RenderMap(){
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),(function (){
return (new mapboxgl.Map(({"container": "smallworld-map", "key": cljs.core.get_in.call(null,smallworld.frontend.mapbox_config,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [smallworld.frontend.mapbox_style,new cljs.core.Keyword(null,"access-token","access-token",-654201199)], null)), "style": cljs.core.get_in.call(null,smallworld.frontend.mapbox_config,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [smallworld.frontend.mapbox_style,new cljs.core.Keyword(null,"style","style",-496642736)], null)), "attributionControl": false, "center": [74.5,(40)], "zoom": (1)})));
}),new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),(function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#smallworld-map","div#smallworld-map",65001223)], null);
})], null));
});
smallworld.frontend.loading_screen = (function smallworld$frontend$loading_screen(){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.center-vh","div.center-vh",1949480392),smallworld.frontend.simple_loading_animation.call(null)], null);
});
smallworld.frontend.logged_out_screen = (function smallworld$frontend$logged_out_screen(){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.welcome.center-vh","div.welcome.center-vh",-1120291834),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h1","h1",-1896887462),"welcome to Small World"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#logo-animation.logo","div#logo-animation.logo",-1750389297),smallworld.frontend.animated_globe.call(null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h2","h2",-372662728),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a#login-btn","a#login-btn",-1155920149),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"href","href",-793805698),"login"], null),"sign in ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.arrow","span.arrow",1996640986),"\u2192"], null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"br","br",934104792)], null),"to connect with friends"], null)], null);
});
smallworld.frontend.logged_in_screen = (function smallworld$frontend$logged_in_screen(){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"<>","<>",1280186386),smallworld.frontend.nav.call(null),(function (){var main_location = new cljs.core.Keyword(null,"main-location","main-location",1133157975).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,smallworld.frontend.current_user));
var name_location = new cljs.core.Keyword(null,"name-location","name-location",-260471193).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,smallworld.frontend.current_user));
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.container","div.container",72419955),smallworld.frontend.Friend.call(null,null,cljs.core.deref.call(null,smallworld.frontend.current_user)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.location-info","div.location-info",-219653806),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"you are based in: ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.location","span.location",-1369691478),main_location], null)], null),(cljs.core.truth_(name_location)?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"your current location: ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.location","span.location",-1369691478),name_location], null)], null):null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"br","br",934104792)], null),((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"loading","loading",-737050189),cljs.core.deref.call(null,smallworld.frontend.friends)))?smallworld.frontend.simple_loading_animation.call(null):new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"<>","<>",1280186386),((cljs.core.empty_QMARK_.call(null,main_location))?null:new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"<>","<>",1280186386),smallworld.frontend.render_friends_list.call(null,new cljs.core.Keyword(null,"main-main","main-main",41668739),"live",main_location),smallworld.frontend.render_friends_list.call(null,new cljs.core.Keyword(null,"main-name","main-name",-2124294023),"are",main_location)], null)),((cljs.core.empty_QMARK_.call(null,name_location))?null:new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"<>","<>",1280186386),smallworld.frontend.render_friends_list.call(null,new cljs.core.Keyword(null,"name-name","name-name",-1767411827),"live",main_location),smallworld.frontend.render_friends_list.call(null,new cljs.core.Keyword(null,"name-main","name-main",1464990880),"are",main_location)], null)),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#smallworld-map-container","div#smallworld-map-container",-1876560630),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a.expand-me","a.expand-me",-1128208442),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (main_location,name_location){
return (function (){
return alert("hi!");
});})(main_location,name_location))
], null),"expand map"], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [smallworld.frontend.RenderMap], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#smallworld-map-spacer","div#smallworld-map-spacer",2011603284)], null)], null)], null))], null);
})()], null);
});
smallworld.frontend.app_container = (function smallworld$frontend$app_container(){
var pred__52004 = cljs.core._EQ_;
var expr__52005 = cljs.core.deref.call(null,smallworld.frontend.current_user);
if(cljs.core.truth_(pred__52004.call(null,new cljs.core.Keyword(null,"loading","loading",-737050189),expr__52005))){
return smallworld.frontend.loading_screen.call(null);
} else {
if(cljs.core.truth_(pred__52004.call(null,smallworld.current_user.default_state,expr__52005))){
return smallworld.frontend.logged_out_screen.call(null);
} else {
return smallworld.frontend.logged_in_screen.call(null);
}
}
});
reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [smallworld.frontend.app_container], null),goog.dom.getElement("app"));

//# sourceMappingURL=frontend.js.map?rel=1642900180947
