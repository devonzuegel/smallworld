// Compiled by ClojureScript 1.10.520 {}
goog.provide('figwheel.client');
goog.require('cljs.core');
goog.require('goog.Uri');
goog.require('goog.userAgent.product');
goog.require('goog.object');
goog.require('cljs.reader');
goog.require('cljs.core.async');
goog.require('figwheel.client.socket');
goog.require('figwheel.client.utils');
goog.require('figwheel.client.heads_up');
goog.require('figwheel.client.file_reloading');
goog.require('clojure.string');
goog.require('cljs.repl');
figwheel.client._figwheel_version_ = "0.5.18";
figwheel.client.js_stringify = (((((typeof JSON !== 'undefined')) && ((!((JSON.stringify == null))))))?(function (x){
return ["#js ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(JSON.stringify(x,null," "))].join('');
}):(function (x){
try{return cljs.core.str.cljs$core$IFn$_invoke$arity$1(x);
}catch (e37487){if((e37487 instanceof Error)){
var e = e37487;
return "Error: Unable to stringify";
} else {
throw e37487;

}
}}));
figwheel.client.figwheel_repl_print = (function figwheel$client$figwheel_repl_print(var_args){
var G__37490 = arguments.length;
switch (G__37490) {
case 2:
return figwheel.client.figwheel_repl_print.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return figwheel.client.figwheel_repl_print.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

figwheel.client.figwheel_repl_print.cljs$core$IFn$_invoke$arity$2 = (function (stream,args){
figwheel.client.socket.send_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"figwheel-event","figwheel-event",519570592),"callback",new cljs.core.Keyword(null,"callback-name","callback-name",336964714),"figwheel-repl-print",new cljs.core.Keyword(null,"content","content",15833224),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"stream","stream",1534941648),stream,new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.mapv.call(null,(function (p1__37488_SHARP_){
if(typeof p1__37488_SHARP_ === 'string'){
return p1__37488_SHARP_;
} else {
return figwheel.client.js_stringify.call(null,p1__37488_SHARP_);
}
}),args)], null)], null));

return null;
});

figwheel.client.figwheel_repl_print.cljs$core$IFn$_invoke$arity$1 = (function (args){
return figwheel.client.figwheel_repl_print.call(null,new cljs.core.Keyword(null,"out","out",-910545517),args);
});

figwheel.client.figwheel_repl_print.cljs$lang$maxFixedArity = 2;

figwheel.client.console_out_print = (function figwheel$client$console_out_print(args){
return console.log.apply(console,cljs.core.into_array.call(null,args));
});
figwheel.client.console_err_print = (function figwheel$client$console_err_print(args){
return console.error.apply(console,cljs.core.into_array.call(null,args));
});
figwheel.client.repl_out_print_fn = (function figwheel$client$repl_out_print_fn(var_args){
var args__4736__auto__ = [];
var len__4730__auto___37493 = arguments.length;
var i__4731__auto___37494 = (0);
while(true){
if((i__4731__auto___37494 < len__4730__auto___37493)){
args__4736__auto__.push((arguments[i__4731__auto___37494]));

var G__37495 = (i__4731__auto___37494 + (1));
i__4731__auto___37494 = G__37495;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return figwheel.client.repl_out_print_fn.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

figwheel.client.repl_out_print_fn.cljs$core$IFn$_invoke$arity$variadic = (function (args){
figwheel.client.console_out_print.call(null,args);

figwheel.client.figwheel_repl_print.call(null,new cljs.core.Keyword(null,"out","out",-910545517),args);

return null;
});

figwheel.client.repl_out_print_fn.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
figwheel.client.repl_out_print_fn.cljs$lang$applyTo = (function (seq37492){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq37492));
});

figwheel.client.repl_err_print_fn = (function figwheel$client$repl_err_print_fn(var_args){
var args__4736__auto__ = [];
var len__4730__auto___37497 = arguments.length;
var i__4731__auto___37498 = (0);
while(true){
if((i__4731__auto___37498 < len__4730__auto___37497)){
args__4736__auto__.push((arguments[i__4731__auto___37498]));

var G__37499 = (i__4731__auto___37498 + (1));
i__4731__auto___37498 = G__37499;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return figwheel.client.repl_err_print_fn.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

figwheel.client.repl_err_print_fn.cljs$core$IFn$_invoke$arity$variadic = (function (args){
figwheel.client.console_err_print.call(null,args);

figwheel.client.figwheel_repl_print.call(null,new cljs.core.Keyword(null,"err","err",-2089457205),args);

return null;
});

figwheel.client.repl_err_print_fn.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
figwheel.client.repl_err_print_fn.cljs$lang$applyTo = (function (seq37496){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq37496));
});

figwheel.client.enable_repl_print_BANG_ = (function figwheel$client$enable_repl_print_BANG_(){
cljs.core._STAR_print_newline_STAR_ = false;

cljs.core.set_print_fn_BANG_.call(null,figwheel.client.repl_out_print_fn);

cljs.core.set_print_err_fn_BANG_.call(null,figwheel.client.repl_err_print_fn);

return null;
});
figwheel.client.autoload_QMARK_ = (function figwheel$client$autoload_QMARK_(){
return figwheel.client.utils.persistent_config_get.call(null,new cljs.core.Keyword(null,"figwheel-autoload","figwheel-autoload",-2044741728),true);
});
figwheel.client.toggle_autoload = (function figwheel$client$toggle_autoload(){
var res = figwheel.client.utils.persistent_config_set_BANG_.call(null,new cljs.core.Keyword(null,"figwheel-autoload","figwheel-autoload",-2044741728),cljs.core.not.call(null,figwheel.client.autoload_QMARK_.call(null)));
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"info","info",-317069002),"Toggle autoload deprecated! Use (figwheel.client/set-autoload! false)");

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"info","info",-317069002),["Figwheel autoloading ",(cljs.core.truth_(figwheel.client.autoload_QMARK_.call(null))?"ON":"OFF")].join(''));

return res;
});
goog.exportSymbol('figwheel.client.toggle_autoload', figwheel.client.toggle_autoload);
/**
 * Figwheel by default loads code changes as you work. Sometimes you
 *   just want to work on your code without the ramifications of
 *   autoloading and simply load your code piecemeal in the REPL. You can
 *   turn autoloading on and of with this method.
 * 
 *   (figwheel.client/set-autoload false)
 * 
 *   NOTE: This is a persistent setting, meaning that it will persist
 *   through browser reloads.
 */
figwheel.client.set_autoload = (function figwheel$client$set_autoload(b){
if(((b === true) || (b === false))){
} else {
throw (new Error("Assert failed: (or (true? b) (false? b))"));
}

return figwheel.client.utils.persistent_config_set_BANG_.call(null,new cljs.core.Keyword(null,"figwheel-autoload","figwheel-autoload",-2044741728),b);
});
goog.exportSymbol('figwheel.client.set_autoload', figwheel.client.set_autoload);
figwheel.client.repl_pprint = (function figwheel$client$repl_pprint(){
return figwheel.client.utils.persistent_config_get.call(null,new cljs.core.Keyword(null,"figwheel-repl-pprint","figwheel-repl-pprint",1076150873),true);
});
goog.exportSymbol('figwheel.client.repl_pprint', figwheel.client.repl_pprint);
/**
 * This method gives you the ability to turn the pretty printing of
 *   the REPL's return value on and off.
 * 
 *   (figwheel.client/set-repl-pprint false)
 * 
 *   NOTE: This is a persistent setting, meaning that it will persist
 *   through browser reloads.
 */
figwheel.client.set_repl_pprint = (function figwheel$client$set_repl_pprint(b){
if(((b === true) || (b === false))){
} else {
throw (new Error("Assert failed: (or (true? b) (false? b))"));
}

return figwheel.client.utils.persistent_config_set_BANG_.call(null,new cljs.core.Keyword(null,"figwheel-repl-pprint","figwheel-repl-pprint",1076150873),b);
});
goog.exportSymbol('figwheel.client.set_repl_pprint', figwheel.client.set_repl_pprint);
figwheel.client.repl_result_pr_str = (function figwheel$client$repl_result_pr_str(v){
if(cljs.core.truth_(figwheel.client.repl_pprint.call(null))){
return figwheel.client.utils.pprint_to_string.call(null,v);
} else {
return cljs.core.pr_str.call(null,v);
}
});
goog.exportSymbol('figwheel.client.repl_result_pr_str', figwheel.client.repl_result_pr_str);
figwheel.client.get_essential_messages = (function figwheel$client$get_essential_messages(ed){
if(cljs.core.truth_(ed)){
return cljs.core.cons.call(null,cljs.core.select_keys.call(null,ed,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.Keyword(null,"class","class",-2030961996)], null)),figwheel.client.get_essential_messages.call(null,new cljs.core.Keyword(null,"cause","cause",231901252).cljs$core$IFn$_invoke$arity$1(ed)));
} else {
return null;
}
});
figwheel.client.error_msg_format = (function figwheel$client$error_msg_format(p__37500){
var map__37501 = p__37500;
var map__37501__$1 = (((((!((map__37501 == null))))?(((((map__37501.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37501.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37501):map__37501);
var message = cljs.core.get.call(null,map__37501__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var class$ = cljs.core.get.call(null,map__37501__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(class$)," : ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(message)].join('');
});
figwheel.client.format_messages = cljs.core.comp.call(null,cljs.core.partial.call(null,cljs.core.map,figwheel.client.error_msg_format),figwheel.client.get_essential_messages);
figwheel.client.focus_msgs = (function figwheel$client$focus_msgs(name_set,msg_hist){
return cljs.core.cons.call(null,cljs.core.first.call(null,msg_hist),cljs.core.filter.call(null,cljs.core.comp.call(null,name_set,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863)),cljs.core.rest.call(null,msg_hist)));
});
figwheel.client.reload_file_QMARK__STAR_ = (function figwheel$client$reload_file_QMARK__STAR_(msg_name,opts){
var or__4131__auto__ = new cljs.core.Keyword(null,"load-warninged-code","load-warninged-code",-2030345223).cljs$core$IFn$_invoke$arity$1(opts);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.not_EQ_.call(null,msg_name,new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356));
}
});
figwheel.client.reload_file_state_QMARK_ = (function figwheel$client$reload_file_state_QMARK_(msg_names,opts){
var and__4120__auto__ = cljs.core._EQ_.call(null,cljs.core.first.call(null,msg_names),new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563));
if(and__4120__auto__){
return figwheel.client.reload_file_QMARK__STAR_.call(null,cljs.core.second.call(null,msg_names),opts);
} else {
return and__4120__auto__;
}
});
figwheel.client.block_reload_file_state_QMARK_ = (function figwheel$client$block_reload_file_state_QMARK_(msg_names,opts){
return ((cljs.core._EQ_.call(null,cljs.core.first.call(null,msg_names),new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563))) && (cljs.core.not.call(null,figwheel.client.reload_file_QMARK__STAR_.call(null,cljs.core.second.call(null,msg_names),opts))));
});
figwheel.client.warning_append_state_QMARK_ = (function figwheel$client$warning_append_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356)], null),cljs.core.take.call(null,(2),msg_names));
});
figwheel.client.warning_state_QMARK_ = (function figwheel$client$warning_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),cljs.core.first.call(null,msg_names));
});
figwheel.client.rewarning_state_QMARK_ = (function figwheel$client$rewarning_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563),new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356)], null),cljs.core.take.call(null,(3),msg_names));
});
figwheel.client.compile_fail_state_QMARK_ = (function figwheel$client$compile_fail_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"compile-failed","compile-failed",-477639289),cljs.core.first.call(null,msg_names));
});
figwheel.client.compile_refail_state_QMARK_ = (function figwheel$client$compile_refail_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"compile-failed","compile-failed",-477639289),new cljs.core.Keyword(null,"compile-failed","compile-failed",-477639289)], null),cljs.core.take.call(null,(2),msg_names));
});
figwheel.client.css_loaded_state_QMARK_ = (function figwheel$client$css_loaded_state_QMARK_(msg_names){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"css-files-changed","css-files-changed",720773874),cljs.core.first.call(null,msg_names));
});
figwheel.client.file_reloader_plugin = (function figwheel$client$file_reloader_plugin(opts){
var ch = cljs.core.async.chan.call(null);
var c__33495__auto___37580 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___37580,ch){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___37580,ch){
return (function (state_37552){
var state_val_37553 = (state_37552[(1)]);
if((state_val_37553 === (7))){
var inst_37548 = (state_37552[(2)]);
var state_37552__$1 = state_37552;
var statearr_37554_37581 = state_37552__$1;
(statearr_37554_37581[(2)] = inst_37548);

(statearr_37554_37581[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (1))){
var state_37552__$1 = state_37552;
var statearr_37555_37582 = state_37552__$1;
(statearr_37555_37582[(2)] = null);

(statearr_37555_37582[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (4))){
var inst_37505 = (state_37552[(7)]);
var inst_37505__$1 = (state_37552[(2)]);
var state_37552__$1 = (function (){var statearr_37556 = state_37552;
(statearr_37556[(7)] = inst_37505__$1);

return statearr_37556;
})();
if(cljs.core.truth_(inst_37505__$1)){
var statearr_37557_37583 = state_37552__$1;
(statearr_37557_37583[(1)] = (5));

} else {
var statearr_37558_37584 = state_37552__$1;
(statearr_37558_37584[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (15))){
var inst_37512 = (state_37552[(8)]);
var inst_37527 = new cljs.core.Keyword(null,"files","files",-472457450).cljs$core$IFn$_invoke$arity$1(inst_37512);
var inst_37528 = cljs.core.first.call(null,inst_37527);
var inst_37529 = new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(inst_37528);
var inst_37530 = ["Figwheel: Not loading code with warnings - ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(inst_37529)].join('');
var inst_37531 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"warn","warn",-436710552),inst_37530);
var state_37552__$1 = state_37552;
var statearr_37559_37585 = state_37552__$1;
(statearr_37559_37585[(2)] = inst_37531);

(statearr_37559_37585[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (13))){
var inst_37536 = (state_37552[(2)]);
var state_37552__$1 = state_37552;
var statearr_37560_37586 = state_37552__$1;
(statearr_37560_37586[(2)] = inst_37536);

(statearr_37560_37586[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (6))){
var state_37552__$1 = state_37552;
var statearr_37561_37587 = state_37552__$1;
(statearr_37561_37587[(2)] = null);

(statearr_37561_37587[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (17))){
var inst_37534 = (state_37552[(2)]);
var state_37552__$1 = state_37552;
var statearr_37562_37588 = state_37552__$1;
(statearr_37562_37588[(2)] = inst_37534);

(statearr_37562_37588[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (3))){
var inst_37550 = (state_37552[(2)]);
var state_37552__$1 = state_37552;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_37552__$1,inst_37550);
} else {
if((state_val_37553 === (12))){
var inst_37511 = (state_37552[(9)]);
var inst_37525 = figwheel.client.block_reload_file_state_QMARK_.call(null,inst_37511,opts);
var state_37552__$1 = state_37552;
if(inst_37525){
var statearr_37563_37589 = state_37552__$1;
(statearr_37563_37589[(1)] = (15));

} else {
var statearr_37564_37590 = state_37552__$1;
(statearr_37564_37590[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (2))){
var state_37552__$1 = state_37552;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37552__$1,(4),ch);
} else {
if((state_val_37553 === (11))){
var inst_37512 = (state_37552[(8)]);
var inst_37517 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_37518 = figwheel.client.file_reloading.reload_js_files.call(null,opts,inst_37512);
var inst_37519 = cljs.core.async.timeout.call(null,(1000));
var inst_37520 = [inst_37518,inst_37519];
var inst_37521 = (new cljs.core.PersistentVector(null,2,(5),inst_37517,inst_37520,null));
var state_37552__$1 = state_37552;
return cljs.core.async.ioc_alts_BANG_.call(null,state_37552__$1,(14),inst_37521);
} else {
if((state_val_37553 === (9))){
var inst_37512 = (state_37552[(8)]);
var inst_37538 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"warn","warn",-436710552),"Figwheel: code autoloading is OFF");
var inst_37539 = new cljs.core.Keyword(null,"files","files",-472457450).cljs$core$IFn$_invoke$arity$1(inst_37512);
var inst_37540 = cljs.core.map.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),inst_37539);
var inst_37541 = ["Not loading: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(inst_37540)].join('');
var inst_37542 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"info","info",-317069002),inst_37541);
var state_37552__$1 = (function (){var statearr_37565 = state_37552;
(statearr_37565[(10)] = inst_37538);

return statearr_37565;
})();
var statearr_37566_37591 = state_37552__$1;
(statearr_37566_37591[(2)] = inst_37542);

(statearr_37566_37591[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (5))){
var inst_37505 = (state_37552[(7)]);
var inst_37507 = [new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),null,new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563),null];
var inst_37508 = (new cljs.core.PersistentArrayMap(null,2,inst_37507,null));
var inst_37509 = (new cljs.core.PersistentHashSet(null,inst_37508,null));
var inst_37510 = figwheel.client.focus_msgs.call(null,inst_37509,inst_37505);
var inst_37511 = cljs.core.map.call(null,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863),inst_37510);
var inst_37512 = cljs.core.first.call(null,inst_37510);
var inst_37513 = figwheel.client.autoload_QMARK_.call(null);
var state_37552__$1 = (function (){var statearr_37567 = state_37552;
(statearr_37567[(8)] = inst_37512);

(statearr_37567[(9)] = inst_37511);

return statearr_37567;
})();
if(cljs.core.truth_(inst_37513)){
var statearr_37568_37592 = state_37552__$1;
(statearr_37568_37592[(1)] = (8));

} else {
var statearr_37569_37593 = state_37552__$1;
(statearr_37569_37593[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (14))){
var inst_37523 = (state_37552[(2)]);
var state_37552__$1 = state_37552;
var statearr_37570_37594 = state_37552__$1;
(statearr_37570_37594[(2)] = inst_37523);

(statearr_37570_37594[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (16))){
var state_37552__$1 = state_37552;
var statearr_37571_37595 = state_37552__$1;
(statearr_37571_37595[(2)] = null);

(statearr_37571_37595[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (10))){
var inst_37544 = (state_37552[(2)]);
var state_37552__$1 = (function (){var statearr_37572 = state_37552;
(statearr_37572[(11)] = inst_37544);

return statearr_37572;
})();
var statearr_37573_37596 = state_37552__$1;
(statearr_37573_37596[(2)] = null);

(statearr_37573_37596[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37553 === (8))){
var inst_37511 = (state_37552[(9)]);
var inst_37515 = figwheel.client.reload_file_state_QMARK_.call(null,inst_37511,opts);
var state_37552__$1 = state_37552;
if(cljs.core.truth_(inst_37515)){
var statearr_37574_37597 = state_37552__$1;
(statearr_37574_37597[(1)] = (11));

} else {
var statearr_37575_37598 = state_37552__$1;
(statearr_37575_37598[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___37580,ch))
;
return ((function (switch__33350__auto__,c__33495__auto___37580,ch){
return (function() {
var figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__ = null;
var figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____0 = (function (){
var statearr_37576 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_37576[(0)] = figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__);

(statearr_37576[(1)] = (1));

return statearr_37576;
});
var figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____1 = (function (state_37552){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_37552);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e37577){if((e37577 instanceof Object)){
var ex__33354__auto__ = e37577;
var statearr_37578_37599 = state_37552;
(statearr_37578_37599[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_37552);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e37577;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__37600 = state_37552;
state_37552 = G__37600;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__ = function(state_37552){
switch(arguments.length){
case 0:
return figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____1.call(this,state_37552);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____0;
figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloader_plugin_$_state_machine__33351__auto____1;
return figwheel$client$file_reloader_plugin_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___37580,ch))
})();
var state__33497__auto__ = (function (){var statearr_37579 = f__33496__auto__.call(null);
(statearr_37579[(6)] = c__33495__auto___37580);

return statearr_37579;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___37580,ch))
);


return ((function (ch){
return (function (msg_hist){
cljs.core.async.put_BANG_.call(null,ch,msg_hist);

return msg_hist;
});
;})(ch))
});
figwheel.client.truncate_stack_trace = (function figwheel$client$truncate_stack_trace(stack_str){
return cljs.core.take_while.call(null,(function (p1__37601_SHARP_){
return cljs.core.not.call(null,cljs.core.re_matches.call(null,/.*eval_javascript_STAR__STAR_.*/,p1__37601_SHARP_));
}),clojure.string.split_lines.call(null,stack_str));
});
figwheel.client.get_ua_product = (function figwheel$client$get_ua_product(){
if(figwheel.client.utils.node_env_QMARK_.call(null)){
return new cljs.core.Keyword(null,"chrome","chrome",1718738387);
} else {
if(cljs.core.truth_(goog.userAgent.product.SAFARI)){
return new cljs.core.Keyword(null,"safari","safari",497115653);
} else {
if(cljs.core.truth_(goog.userAgent.product.CHROME)){
return new cljs.core.Keyword(null,"chrome","chrome",1718738387);
} else {
if(cljs.core.truth_(goog.userAgent.product.FIREFOX)){
return new cljs.core.Keyword(null,"firefox","firefox",1283768880);
} else {
if(cljs.core.truth_(goog.userAgent.product.IE)){
return new cljs.core.Keyword(null,"ie","ie",2038473780);
} else {
return null;
}
}
}
}
}
});
var base_path_37607 = figwheel.client.utils.base_url_path.call(null);
figwheel.client.eval_javascript_STAR__STAR_ = ((function (base_path_37607){
return (function figwheel$client$eval_javascript_STAR__STAR_(code,opts,result_handler){
try{var sb = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__37603 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__37604 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__37605 = true;
var _STAR_print_fn_STAR__temp_val__37606 = ((function (_STAR_print_newline_STAR__orig_val__37603,_STAR_print_fn_STAR__orig_val__37604,_STAR_print_newline_STAR__temp_val__37605,sb,base_path_37607){
return (function (x){
return sb.append(x);
});})(_STAR_print_newline_STAR__orig_val__37603,_STAR_print_fn_STAR__orig_val__37604,_STAR_print_newline_STAR__temp_val__37605,sb,base_path_37607))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__37605;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__37606;

try{var result_value = figwheel.client.utils.eval_helper.call(null,code,opts);
var result_value__$1 = (((!(typeof result_value === 'string')))?cljs.core.pr_str.call(null,result_value):result_value);
return result_handler.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"success","success",1890645906),new cljs.core.Keyword(null,"out","out",-910545517),cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb),new cljs.core.Keyword(null,"ua-product","ua-product",938384227),figwheel.client.get_ua_product.call(null),new cljs.core.Keyword(null,"value","value",305978217),result_value__$1], null));
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__37604;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__37603;
}}catch (e37602){if((e37602 instanceof Error)){
var e = e37602;
return result_handler.call(null,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"exception","exception",-335277064),new cljs.core.Keyword(null,"value","value",305978217),cljs.core.pr_str.call(null,e),new cljs.core.Keyword(null,"ua-product","ua-product",938384227),figwheel.client.get_ua_product.call(null),new cljs.core.Keyword(null,"stacktrace","stacktrace",-95588394),clojure.string.join.call(null,"\n",figwheel.client.truncate_stack_trace.call(null,e.stack)),new cljs.core.Keyword(null,"base-path","base-path",495760020),base_path_37607], null));
} else {
var e = e37602;
return result_handler.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"exception","exception",-335277064),new cljs.core.Keyword(null,"ua-product","ua-product",938384227),figwheel.client.get_ua_product.call(null),new cljs.core.Keyword(null,"value","value",305978217),cljs.core.pr_str.call(null,e),new cljs.core.Keyword(null,"stacktrace","stacktrace",-95588394),"No stacktrace available."], null));

}
}});})(base_path_37607))
;
/**
 * The REPL can disconnect and reconnect lets ensure cljs.user exists at least.
 */
figwheel.client.ensure_cljs_user = (function figwheel$client$ensure_cljs_user(){
if(cljs.core.truth_(cljs.user)){
return null;
} else {
return cljs.user = ({});
}
});
figwheel.client.repl_plugin = (function figwheel$client$repl_plugin(p__37608){
var map__37609 = p__37608;
var map__37609__$1 = (((((!((map__37609 == null))))?(((((map__37609.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37609.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37609):map__37609);
var opts = map__37609__$1;
var build_id = cljs.core.get.call(null,map__37609__$1,new cljs.core.Keyword(null,"build-id","build-id",1642831089));
return ((function (map__37609,map__37609__$1,opts,build_id){
return (function (p__37611){
var vec__37612 = p__37611;
var seq__37613 = cljs.core.seq.call(null,vec__37612);
var first__37614 = cljs.core.first.call(null,seq__37613);
var seq__37613__$1 = cljs.core.next.call(null,seq__37613);
var map__37615 = first__37614;
var map__37615__$1 = (((((!((map__37615 == null))))?(((((map__37615.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37615.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37615):map__37615);
var msg = map__37615__$1;
var msg_name = cljs.core.get.call(null,map__37615__$1,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863));
var _ = seq__37613__$1;
if(cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"repl-eval","repl-eval",-1784727398),msg_name)){
figwheel.client.ensure_cljs_user.call(null);

return figwheel.client.eval_javascript_STAR__STAR_.call(null,new cljs.core.Keyword(null,"code","code",1586293142).cljs$core$IFn$_invoke$arity$1(msg),opts,((function (vec__37612,seq__37613,first__37614,seq__37613__$1,map__37615,map__37615__$1,msg,msg_name,_,map__37609,map__37609__$1,opts,build_id){
return (function (res){
return figwheel.client.socket.send_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"figwheel-event","figwheel-event",519570592),"callback",new cljs.core.Keyword(null,"callback-name","callback-name",336964714),new cljs.core.Keyword(null,"callback-name","callback-name",336964714).cljs$core$IFn$_invoke$arity$1(msg),new cljs.core.Keyword(null,"content","content",15833224),res], null));
});})(vec__37612,seq__37613,first__37614,seq__37613__$1,map__37615,map__37615__$1,msg,msg_name,_,map__37609,map__37609__$1,opts,build_id))
);
} else {
return null;
}
});
;})(map__37609,map__37609__$1,opts,build_id))
});
figwheel.client.css_reloader_plugin = (function figwheel$client$css_reloader_plugin(opts){
return (function (p__37617){
var vec__37618 = p__37617;
var seq__37619 = cljs.core.seq.call(null,vec__37618);
var first__37620 = cljs.core.first.call(null,seq__37619);
var seq__37619__$1 = cljs.core.next.call(null,seq__37619);
var map__37621 = first__37620;
var map__37621__$1 = (((((!((map__37621 == null))))?(((((map__37621.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37621.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37621):map__37621);
var msg = map__37621__$1;
var msg_name = cljs.core.get.call(null,map__37621__$1,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863));
var _ = seq__37619__$1;
if(cljs.core._EQ_.call(null,msg_name,new cljs.core.Keyword(null,"css-files-changed","css-files-changed",720773874))){
return figwheel.client.file_reloading.reload_css_files.call(null,opts,msg);
} else {
return null;
}
});
});
figwheel.client.compile_fail_warning_plugin = (function figwheel$client$compile_fail_warning_plugin(p__37623){
var map__37624 = p__37623;
var map__37624__$1 = (((((!((map__37624 == null))))?(((((map__37624.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37624.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37624):map__37624);
var on_compile_warning = cljs.core.get.call(null,map__37624__$1,new cljs.core.Keyword(null,"on-compile-warning","on-compile-warning",-1195585947));
var on_compile_fail = cljs.core.get.call(null,map__37624__$1,new cljs.core.Keyword(null,"on-compile-fail","on-compile-fail",728013036));
return ((function (map__37624,map__37624__$1,on_compile_warning,on_compile_fail){
return (function (p__37626){
var vec__37627 = p__37626;
var seq__37628 = cljs.core.seq.call(null,vec__37627);
var first__37629 = cljs.core.first.call(null,seq__37628);
var seq__37628__$1 = cljs.core.next.call(null,seq__37628);
var map__37630 = first__37629;
var map__37630__$1 = (((((!((map__37630 == null))))?(((((map__37630.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37630.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37630):map__37630);
var msg = map__37630__$1;
var msg_name = cljs.core.get.call(null,map__37630__$1,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863));
var _ = seq__37628__$1;
var pred__37632 = cljs.core._EQ_;
var expr__37633 = msg_name;
if(cljs.core.truth_(pred__37632.call(null,new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),expr__37633))){
return on_compile_warning.call(null,msg);
} else {
if(cljs.core.truth_(pred__37632.call(null,new cljs.core.Keyword(null,"compile-failed","compile-failed",-477639289),expr__37633))){
return on_compile_fail.call(null,msg);
} else {
return null;
}
}
});
;})(map__37624,map__37624__$1,on_compile_warning,on_compile_fail))
});
figwheel.client.auto_jump_to_error = (function figwheel$client$auto_jump_to_error(opts,error){
if(cljs.core.truth_(new cljs.core.Keyword(null,"auto-jump-to-source-on-error","auto-jump-to-source-on-error",-960314920).cljs$core$IFn$_invoke$arity$1(opts))){
return figwheel.client.heads_up.auto_notify_source_file_line.call(null,error);
} else {
return null;
}
});
figwheel.client.heads_up_plugin_msg_handler = (function figwheel$client$heads_up_plugin_msg_handler(opts,msg_hist_SINGLEQUOTE_){
var msg_hist = figwheel.client.focus_msgs.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"compile-failed","compile-failed",-477639289),null,new cljs.core.Keyword(null,"compile-warning","compile-warning",43425356),null,new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563),null], null), null),msg_hist_SINGLEQUOTE_);
var msg_names = cljs.core.map.call(null,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863),msg_hist);
var msg = cljs.core.first.call(null,msg_hist);
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__,msg_hist,msg_names,msg){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__,msg_hist,msg_names,msg){
return (function (state_37722){
var state_val_37723 = (state_37722[(1)]);
if((state_val_37723 === (7))){
var inst_37642 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
if(cljs.core.truth_(inst_37642)){
var statearr_37724_37771 = state_37722__$1;
(statearr_37724_37771[(1)] = (8));

} else {
var statearr_37725_37772 = state_37722__$1;
(statearr_37725_37772[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (20))){
var inst_37716 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37726_37773 = state_37722__$1;
(statearr_37726_37773[(2)] = inst_37716);

(statearr_37726_37773[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (27))){
var inst_37712 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37727_37774 = state_37722__$1;
(statearr_37727_37774[(2)] = inst_37712);

(statearr_37727_37774[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (1))){
var inst_37635 = figwheel.client.reload_file_state_QMARK_.call(null,msg_names,opts);
var state_37722__$1 = state_37722;
if(cljs.core.truth_(inst_37635)){
var statearr_37728_37775 = state_37722__$1;
(statearr_37728_37775[(1)] = (2));

} else {
var statearr_37729_37776 = state_37722__$1;
(statearr_37729_37776[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (24))){
var inst_37714 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37730_37777 = state_37722__$1;
(statearr_37730_37777[(2)] = inst_37714);

(statearr_37730_37777[(1)] = (20));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (4))){
var inst_37720 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_37722__$1,inst_37720);
} else {
if((state_val_37723 === (15))){
var inst_37718 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37731_37778 = state_37722__$1;
(statearr_37731_37778[(2)] = inst_37718);

(statearr_37731_37778[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (21))){
var inst_37671 = (state_37722[(2)]);
var inst_37672 = new cljs.core.Keyword(null,"exception-data","exception-data",-512474886).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37673 = figwheel.client.auto_jump_to_error.call(null,opts,inst_37672);
var state_37722__$1 = (function (){var statearr_37732 = state_37722;
(statearr_37732[(7)] = inst_37671);

return statearr_37732;
})();
var statearr_37733_37779 = state_37722__$1;
(statearr_37733_37779[(2)] = inst_37673);

(statearr_37733_37779[(1)] = (20));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (31))){
var inst_37701 = figwheel.client.css_loaded_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37701){
var statearr_37734_37780 = state_37722__$1;
(statearr_37734_37780[(1)] = (34));

} else {
var statearr_37735_37781 = state_37722__$1;
(statearr_37735_37781[(1)] = (35));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (32))){
var inst_37710 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37736_37782 = state_37722__$1;
(statearr_37736_37782[(2)] = inst_37710);

(statearr_37736_37782[(1)] = (27));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (33))){
var inst_37697 = (state_37722[(2)]);
var inst_37698 = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37699 = figwheel.client.auto_jump_to_error.call(null,opts,inst_37698);
var state_37722__$1 = (function (){var statearr_37737 = state_37722;
(statearr_37737[(8)] = inst_37697);

return statearr_37737;
})();
var statearr_37738_37783 = state_37722__$1;
(statearr_37738_37783[(2)] = inst_37699);

(statearr_37738_37783[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (13))){
var inst_37656 = figwheel.client.heads_up.clear.call(null);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(16),inst_37656);
} else {
if((state_val_37723 === (22))){
var inst_37677 = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37678 = figwheel.client.heads_up.append_warning_message.call(null,inst_37677);
var state_37722__$1 = state_37722;
var statearr_37739_37784 = state_37722__$1;
(statearr_37739_37784[(2)] = inst_37678);

(statearr_37739_37784[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (36))){
var inst_37708 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37740_37785 = state_37722__$1;
(statearr_37740_37785[(2)] = inst_37708);

(statearr_37740_37785[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (29))){
var inst_37688 = (state_37722[(2)]);
var inst_37689 = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37690 = figwheel.client.auto_jump_to_error.call(null,opts,inst_37689);
var state_37722__$1 = (function (){var statearr_37741 = state_37722;
(statearr_37741[(9)] = inst_37688);

return statearr_37741;
})();
var statearr_37742_37786 = state_37722__$1;
(statearr_37742_37786[(2)] = inst_37690);

(statearr_37742_37786[(1)] = (27));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (6))){
var inst_37637 = (state_37722[(10)]);
var state_37722__$1 = state_37722;
var statearr_37743_37787 = state_37722__$1;
(statearr_37743_37787[(2)] = inst_37637);

(statearr_37743_37787[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (28))){
var inst_37684 = (state_37722[(2)]);
var inst_37685 = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37686 = figwheel.client.heads_up.display_warning.call(null,inst_37685);
var state_37722__$1 = (function (){var statearr_37744 = state_37722;
(statearr_37744[(11)] = inst_37684);

return statearr_37744;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(29),inst_37686);
} else {
if((state_val_37723 === (25))){
var inst_37682 = figwheel.client.heads_up.clear.call(null);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(28),inst_37682);
} else {
if((state_val_37723 === (34))){
var inst_37703 = figwheel.client.heads_up.flash_loaded.call(null);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(37),inst_37703);
} else {
if((state_val_37723 === (17))){
var inst_37662 = (state_37722[(2)]);
var inst_37663 = new cljs.core.Keyword(null,"exception-data","exception-data",-512474886).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37664 = figwheel.client.auto_jump_to_error.call(null,opts,inst_37663);
var state_37722__$1 = (function (){var statearr_37745 = state_37722;
(statearr_37745[(12)] = inst_37662);

return statearr_37745;
})();
var statearr_37746_37788 = state_37722__$1;
(statearr_37746_37788[(2)] = inst_37664);

(statearr_37746_37788[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (3))){
var inst_37654 = figwheel.client.compile_refail_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37654){
var statearr_37747_37789 = state_37722__$1;
(statearr_37747_37789[(1)] = (13));

} else {
var statearr_37748_37790 = state_37722__$1;
(statearr_37748_37790[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (12))){
var inst_37650 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37749_37791 = state_37722__$1;
(statearr_37749_37791[(2)] = inst_37650);

(statearr_37749_37791[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (2))){
var inst_37637 = (state_37722[(10)]);
var inst_37637__$1 = figwheel.client.autoload_QMARK_.call(null);
var state_37722__$1 = (function (){var statearr_37750 = state_37722;
(statearr_37750[(10)] = inst_37637__$1);

return statearr_37750;
})();
if(cljs.core.truth_(inst_37637__$1)){
var statearr_37751_37792 = state_37722__$1;
(statearr_37751_37792[(1)] = (5));

} else {
var statearr_37752_37793 = state_37722__$1;
(statearr_37752_37793[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (23))){
var inst_37680 = figwheel.client.rewarning_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37680){
var statearr_37753_37794 = state_37722__$1;
(statearr_37753_37794[(1)] = (25));

} else {
var statearr_37754_37795 = state_37722__$1;
(statearr_37754_37795[(1)] = (26));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (35))){
var state_37722__$1 = state_37722;
var statearr_37755_37796 = state_37722__$1;
(statearr_37755_37796[(2)] = null);

(statearr_37755_37796[(1)] = (36));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (19))){
var inst_37675 = figwheel.client.warning_append_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37675){
var statearr_37756_37797 = state_37722__$1;
(statearr_37756_37797[(1)] = (22));

} else {
var statearr_37757_37798 = state_37722__$1;
(statearr_37757_37798[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (11))){
var inst_37646 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37758_37799 = state_37722__$1;
(statearr_37758_37799[(2)] = inst_37646);

(statearr_37758_37799[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (9))){
var inst_37648 = figwheel.client.heads_up.clear.call(null);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(12),inst_37648);
} else {
if((state_val_37723 === (5))){
var inst_37639 = new cljs.core.Keyword(null,"autoload","autoload",-354122500).cljs$core$IFn$_invoke$arity$1(opts);
var state_37722__$1 = state_37722;
var statearr_37759_37800 = state_37722__$1;
(statearr_37759_37800[(2)] = inst_37639);

(statearr_37759_37800[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (14))){
var inst_37666 = figwheel.client.compile_fail_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37666){
var statearr_37760_37801 = state_37722__$1;
(statearr_37760_37801[(1)] = (18));

} else {
var statearr_37761_37802 = state_37722__$1;
(statearr_37761_37802[(1)] = (19));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (26))){
var inst_37692 = figwheel.client.warning_state_QMARK_.call(null,msg_names);
var state_37722__$1 = state_37722;
if(inst_37692){
var statearr_37762_37803 = state_37722__$1;
(statearr_37762_37803[(1)] = (30));

} else {
var statearr_37763_37804 = state_37722__$1;
(statearr_37763_37804[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (16))){
var inst_37658 = (state_37722[(2)]);
var inst_37659 = new cljs.core.Keyword(null,"exception-data","exception-data",-512474886).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37660 = figwheel.client.heads_up.display_exception.call(null,inst_37659);
var state_37722__$1 = (function (){var statearr_37764 = state_37722;
(statearr_37764[(13)] = inst_37658);

return statearr_37764;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(17),inst_37660);
} else {
if((state_val_37723 === (30))){
var inst_37694 = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37695 = figwheel.client.heads_up.display_warning.call(null,inst_37694);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(33),inst_37695);
} else {
if((state_val_37723 === (10))){
var inst_37652 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37765_37805 = state_37722__$1;
(statearr_37765_37805[(2)] = inst_37652);

(statearr_37765_37805[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (18))){
var inst_37668 = new cljs.core.Keyword(null,"exception-data","exception-data",-512474886).cljs$core$IFn$_invoke$arity$1(msg);
var inst_37669 = figwheel.client.heads_up.display_exception.call(null,inst_37668);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(21),inst_37669);
} else {
if((state_val_37723 === (37))){
var inst_37705 = (state_37722[(2)]);
var state_37722__$1 = state_37722;
var statearr_37766_37806 = state_37722__$1;
(statearr_37766_37806[(2)] = inst_37705);

(statearr_37766_37806[(1)] = (36));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37723 === (8))){
var inst_37644 = figwheel.client.heads_up.flash_loaded.call(null);
var state_37722__$1 = state_37722;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37722__$1,(11),inst_37644);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto__,msg_hist,msg_names,msg))
;
return ((function (switch__33350__auto__,c__33495__auto__,msg_hist,msg_names,msg){
return (function() {
var figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__ = null;
var figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____0 = (function (){
var statearr_37767 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_37767[(0)] = figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__);

(statearr_37767[(1)] = (1));

return statearr_37767;
});
var figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____1 = (function (state_37722){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_37722);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e37768){if((e37768 instanceof Object)){
var ex__33354__auto__ = e37768;
var statearr_37769_37807 = state_37722;
(statearr_37769_37807[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_37722);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e37768;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__37808 = state_37722;
state_37722 = G__37808;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__ = function(state_37722){
switch(arguments.length){
case 0:
return figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____1.call(this,state_37722);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____0;
figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto____1;
return figwheel$client$heads_up_plugin_msg_handler_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__,msg_hist,msg_names,msg))
})();
var state__33497__auto__ = (function (){var statearr_37770 = f__33496__auto__.call(null);
(statearr_37770[(6)] = c__33495__auto__);

return statearr_37770;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__,msg_hist,msg_names,msg))
);

return c__33495__auto__;
});
figwheel.client.heads_up_plugin = (function figwheel$client$heads_up_plugin(opts){
var ch = cljs.core.async.chan.call(null);
figwheel.client.heads_up_config_options_STAR__STAR_ = opts;

var c__33495__auto___37837 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___37837,ch){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___37837,ch){
return (function (state_37823){
var state_val_37824 = (state_37823[(1)]);
if((state_val_37824 === (1))){
var state_37823__$1 = state_37823;
var statearr_37825_37838 = state_37823__$1;
(statearr_37825_37838[(2)] = null);

(statearr_37825_37838[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37824 === (2))){
var state_37823__$1 = state_37823;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37823__$1,(4),ch);
} else {
if((state_val_37824 === (3))){
var inst_37821 = (state_37823[(2)]);
var state_37823__$1 = state_37823;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_37823__$1,inst_37821);
} else {
if((state_val_37824 === (4))){
var inst_37811 = (state_37823[(7)]);
var inst_37811__$1 = (state_37823[(2)]);
var state_37823__$1 = (function (){var statearr_37826 = state_37823;
(statearr_37826[(7)] = inst_37811__$1);

return statearr_37826;
})();
if(cljs.core.truth_(inst_37811__$1)){
var statearr_37827_37839 = state_37823__$1;
(statearr_37827_37839[(1)] = (5));

} else {
var statearr_37828_37840 = state_37823__$1;
(statearr_37828_37840[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37824 === (5))){
var inst_37811 = (state_37823[(7)]);
var inst_37813 = figwheel.client.heads_up_plugin_msg_handler.call(null,opts,inst_37811);
var state_37823__$1 = state_37823;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37823__$1,(8),inst_37813);
} else {
if((state_val_37824 === (6))){
var state_37823__$1 = state_37823;
var statearr_37829_37841 = state_37823__$1;
(statearr_37829_37841[(2)] = null);

(statearr_37829_37841[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37824 === (7))){
var inst_37819 = (state_37823[(2)]);
var state_37823__$1 = state_37823;
var statearr_37830_37842 = state_37823__$1;
(statearr_37830_37842[(2)] = inst_37819);

(statearr_37830_37842[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_37824 === (8))){
var inst_37815 = (state_37823[(2)]);
var state_37823__$1 = (function (){var statearr_37831 = state_37823;
(statearr_37831[(8)] = inst_37815);

return statearr_37831;
})();
var statearr_37832_37843 = state_37823__$1;
(statearr_37832_37843[(2)] = null);

(statearr_37832_37843[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
});})(c__33495__auto___37837,ch))
;
return ((function (switch__33350__auto__,c__33495__auto___37837,ch){
return (function() {
var figwheel$client$heads_up_plugin_$_state_machine__33351__auto__ = null;
var figwheel$client$heads_up_plugin_$_state_machine__33351__auto____0 = (function (){
var statearr_37833 = [null,null,null,null,null,null,null,null,null];
(statearr_37833[(0)] = figwheel$client$heads_up_plugin_$_state_machine__33351__auto__);

(statearr_37833[(1)] = (1));

return statearr_37833;
});
var figwheel$client$heads_up_plugin_$_state_machine__33351__auto____1 = (function (state_37823){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_37823);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e37834){if((e37834 instanceof Object)){
var ex__33354__auto__ = e37834;
var statearr_37835_37844 = state_37823;
(statearr_37835_37844[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_37823);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e37834;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__37845 = state_37823;
state_37823 = G__37845;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$heads_up_plugin_$_state_machine__33351__auto__ = function(state_37823){
switch(arguments.length){
case 0:
return figwheel$client$heads_up_plugin_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$heads_up_plugin_$_state_machine__33351__auto____1.call(this,state_37823);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$heads_up_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$heads_up_plugin_$_state_machine__33351__auto____0;
figwheel$client$heads_up_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$heads_up_plugin_$_state_machine__33351__auto____1;
return figwheel$client$heads_up_plugin_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___37837,ch))
})();
var state__33497__auto__ = (function (){var statearr_37836 = f__33496__auto__.call(null);
(statearr_37836[(6)] = c__33495__auto___37837);

return statearr_37836;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___37837,ch))
);


figwheel.client.heads_up.ensure_container.call(null);

return ((function (ch){
return (function (msg_hist){
cljs.core.async.put_BANG_.call(null,ch,msg_hist);

return msg_hist;
});
;})(ch))
});
figwheel.client.enforce_project_plugin = (function figwheel$client$enforce_project_plugin(opts){
return (function (msg_hist){
if(((1) < cljs.core.count.call(null,cljs.core.set.call(null,cljs.core.keep.call(null,new cljs.core.Keyword(null,"project-id","project-id",206449307),cljs.core.take.call(null,(5),msg_hist)))))){
figwheel.client.socket.close_BANG_.call(null);

console.error("Figwheel: message received from different project. Shutting socket down.");

if(cljs.core.truth_(new cljs.core.Keyword(null,"heads-up-display","heads-up-display",-896577202).cljs$core$IFn$_invoke$arity$1(opts))){
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__){
return (function (state_37851){
var state_val_37852 = (state_37851[(1)]);
if((state_val_37852 === (1))){
var inst_37846 = cljs.core.async.timeout.call(null,(3000));
var state_37851__$1 = state_37851;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37851__$1,(2),inst_37846);
} else {
if((state_val_37852 === (2))){
var inst_37848 = (state_37851[(2)]);
var inst_37849 = figwheel.client.heads_up.display_system_warning.call(null,"Connection from different project","Shutting connection down!!!!!");
var state_37851__$1 = (function (){var statearr_37853 = state_37851;
(statearr_37853[(7)] = inst_37848);

return statearr_37853;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_37851__$1,inst_37849);
} else {
return null;
}
}
});})(c__33495__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__){
return (function() {
var figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__ = null;
var figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____0 = (function (){
var statearr_37854 = [null,null,null,null,null,null,null,null];
(statearr_37854[(0)] = figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__);

(statearr_37854[(1)] = (1));

return statearr_37854;
});
var figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____1 = (function (state_37851){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_37851);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e37855){if((e37855 instanceof Object)){
var ex__33354__auto__ = e37855;
var statearr_37856_37858 = state_37851;
(statearr_37856_37858[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_37851);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e37855;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__37859 = state_37851;
state_37851 = G__37859;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__ = function(state_37851){
switch(arguments.length){
case 0:
return figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____1.call(this,state_37851);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____0;
figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$enforce_project_plugin_$_state_machine__33351__auto____1;
return figwheel$client$enforce_project_plugin_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__))
})();
var state__33497__auto__ = (function (){var statearr_37857 = f__33496__auto__.call(null);
(statearr_37857[(6)] = c__33495__auto__);

return statearr_37857;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__))
);

return c__33495__auto__;
} else {
return null;
}
} else {
return null;
}
});
});
figwheel.client.enforce_figwheel_version_plugin = (function figwheel$client$enforce_figwheel_version_plugin(opts){
return (function (msg_hist){
var temp__5753__auto__ = new cljs.core.Keyword(null,"figwheel-version","figwheel-version",1409553832).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,msg_hist));
if(cljs.core.truth_(temp__5753__auto__)){
var figwheel_version = temp__5753__auto__;
if(cljs.core.not_EQ_.call(null,figwheel_version,figwheel.client._figwheel_version_)){
figwheel.client.socket.close_BANG_.call(null);

console.error("Figwheel: message received from different version of Figwheel.");

if(cljs.core.truth_(new cljs.core.Keyword(null,"heads-up-display","heads-up-display",-896577202).cljs$core$IFn$_invoke$arity$1(opts))){
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__,figwheel_version,temp__5753__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__,figwheel_version,temp__5753__auto__){
return (function (state_37866){
var state_val_37867 = (state_37866[(1)]);
if((state_val_37867 === (1))){
var inst_37860 = cljs.core.async.timeout.call(null,(2000));
var state_37866__$1 = state_37866;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_37866__$1,(2),inst_37860);
} else {
if((state_val_37867 === (2))){
var inst_37862 = (state_37866[(2)]);
var inst_37863 = ["Figwheel Client Version <strong>",figwheel.client._figwheel_version_,"</strong> is not equal to ","Figwheel Sidecar Version <strong>",cljs.core.str.cljs$core$IFn$_invoke$arity$1(figwheel_version),"</strong>",".  Shutting down Websocket Connection!","<h4>To fix try:</h4>","<ol><li>Reload this page and make sure you are not getting a cached version of the client.</li>","<li>You may have to clean (delete compiled assets) and rebuild to make sure that the new client code is being used.</li>","<li>Also, make sure you have consistent Figwheel dependencies.</li></ol>"].join('');
var inst_37864 = figwheel.client.heads_up.display_system_warning.call(null,"Figwheel Client and Server have different versions!!",inst_37863);
var state_37866__$1 = (function (){var statearr_37868 = state_37866;
(statearr_37868[(7)] = inst_37862);

return statearr_37868;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_37866__$1,inst_37864);
} else {
return null;
}
}
});})(c__33495__auto__,figwheel_version,temp__5753__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__,figwheel_version,temp__5753__auto__){
return (function() {
var figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__ = null;
var figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____0 = (function (){
var statearr_37869 = [null,null,null,null,null,null,null,null];
(statearr_37869[(0)] = figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__);

(statearr_37869[(1)] = (1));

return statearr_37869;
});
var figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____1 = (function (state_37866){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_37866);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e37870){if((e37870 instanceof Object)){
var ex__33354__auto__ = e37870;
var statearr_37871_37873 = state_37866;
(statearr_37871_37873[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_37866);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e37870;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__37874 = state_37866;
state_37866 = G__37874;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__ = function(state_37866){
switch(arguments.length){
case 0:
return figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____1.call(this,state_37866);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____0;
figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto____1;
return figwheel$client$enforce_figwheel_version_plugin_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__,figwheel_version,temp__5753__auto__))
})();
var state__33497__auto__ = (function (){var statearr_37872 = f__33496__auto__.call(null);
(statearr_37872[(6)] = c__33495__auto__);

return statearr_37872;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__,figwheel_version,temp__5753__auto__))
);

return c__33495__auto__;
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
});
});
figwheel.client.default_on_jsload = cljs.core.identity;
figwheel.client.file_line_column = (function figwheel$client$file_line_column(p__37875){
var map__37876 = p__37875;
var map__37876__$1 = (((((!((map__37876 == null))))?(((((map__37876.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37876.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37876):map__37876);
var file = cljs.core.get.call(null,map__37876__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
var line = cljs.core.get.call(null,map__37876__$1,new cljs.core.Keyword(null,"line","line",212345235));
var column = cljs.core.get.call(null,map__37876__$1,new cljs.core.Keyword(null,"column","column",2078222095));
var G__37878 = "";
var G__37878__$1 = (cljs.core.truth_(file)?[G__37878,"file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(file)].join(''):G__37878);
var G__37878__$2 = (cljs.core.truth_(line)?[G__37878__$1," at line ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(line)].join(''):G__37878__$1);
if(cljs.core.truth_((function (){var and__4120__auto__ = line;
if(cljs.core.truth_(and__4120__auto__)){
return column;
} else {
return and__4120__auto__;
}
})())){
return [G__37878__$2,", column ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column)].join('');
} else {
return G__37878__$2;
}
});
figwheel.client.default_on_compile_fail = (function figwheel$client$default_on_compile_fail(p__37879){
var map__37880 = p__37879;
var map__37880__$1 = (((((!((map__37880 == null))))?(((((map__37880.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37880.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37880):map__37880);
var ed = map__37880__$1;
var exception_data = cljs.core.get.call(null,map__37880__$1,new cljs.core.Keyword(null,"exception-data","exception-data",-512474886));
var cause = cljs.core.get.call(null,map__37880__$1,new cljs.core.Keyword(null,"cause","cause",231901252));
var message_37883 = (function (){var G__37882 = cljs.core.apply.call(null,cljs.core.str,"Figwheel: Compile Exception ",figwheel.client.format_messages.call(null,exception_data));
if(cljs.core.truth_(new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(exception_data))){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__37882)," Error on ",figwheel.client.file_line_column.call(null,exception_data)].join('');
} else {
return G__37882;
}
})();
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"warn","warn",-436710552),message_37883);

return ed;
});
figwheel.client.default_on_compile_warning = (function figwheel$client$default_on_compile_warning(p__37884){
var map__37885 = p__37884;
var map__37885__$1 = (((((!((map__37885 == null))))?(((((map__37885.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37885.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37885):map__37885);
var w = map__37885__$1;
var message = cljs.core.get.call(null,map__37885__$1,new cljs.core.Keyword(null,"message","message",-406056002));
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"warn","warn",-436710552),["Figwheel: Compile Warning - ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(message))," in ",figwheel.client.file_line_column.call(null,message)].join(''));

return w;
});
figwheel.client.default_before_load = (function figwheel$client$default_before_load(files){
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: notified of file changes");

return files;
});
figwheel.client.default_on_cssload = (function figwheel$client$default_on_cssload(files){
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: loaded CSS files");

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"info","info",-317069002),cljs.core.pr_str.call(null,cljs.core.map.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),files)));

return files;
});
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.config_defaults !== 'undefined')){
} else {
figwheel.client.config_defaults = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"on-compile-warning","on-compile-warning",-1195585947),new cljs.core.Keyword(null,"on-jsload","on-jsload",-395756602),new cljs.core.Keyword(null,"reload-dependents","reload-dependents",-956865430),new cljs.core.Keyword(null,"on-compile-fail","on-compile-fail",728013036),new cljs.core.Keyword(null,"debug","debug",-1608172596),new cljs.core.Keyword(null,"heads-up-display","heads-up-display",-896577202),new cljs.core.Keyword(null,"websocket-url","websocket-url",-490444938),new cljs.core.Keyword(null,"auto-jump-to-source-on-error","auto-jump-to-source-on-error",-960314920),new cljs.core.Keyword(null,"before-jsload","before-jsload",-847513128),new cljs.core.Keyword(null,"load-warninged-code","load-warninged-code",-2030345223),new cljs.core.Keyword(null,"eval-fn","eval-fn",-1111644294),new cljs.core.Keyword(null,"retry-count","retry-count",1936122875),new cljs.core.Keyword(null,"autoload","autoload",-354122500),new cljs.core.Keyword(null,"on-cssload","on-cssload",1825432318)],[new cljs.core.Var(function(){return figwheel.client.default_on_compile_warning;},new cljs.core.Symbol("figwheel.client","default-on-compile-warning","figwheel.client/default-on-compile-warning",584144208,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[new cljs.core.Symbol(null,"figwheel.client","figwheel.client",-538710252,null),new cljs.core.Symbol(null,"default-on-compile-warning","default-on-compile-warning",-18911586,null),"resources/public/js/out/figwheel/client.cljs",33,1,362,362,cljs.core.list(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"keys","keys",1068423698),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"message","message",1234475525,null)], null),new cljs.core.Keyword(null,"as","as",1148689641),new cljs.core.Symbol(null,"w","w",1994700528,null)], null)], null)),null,(cljs.core.truth_(figwheel.client.default_on_compile_warning)?figwheel.client.default_on_compile_warning.cljs$lang$test:null)])),figwheel.client.default_on_jsload,true,new cljs.core.Var(function(){return figwheel.client.default_on_compile_fail;},new cljs.core.Symbol("figwheel.client","default-on-compile-fail","figwheel.client/default-on-compile-fail",1384826337,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[new cljs.core.Symbol(null,"figwheel.client","figwheel.client",-538710252,null),new cljs.core.Symbol(null,"default-on-compile-fail","default-on-compile-fail",-158814813,null),"resources/public/js/out/figwheel/client.cljs",30,1,355,355,cljs.core.list(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"keys","keys",1068423698),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"exception-data","exception-data",1128056641,null),new cljs.core.Symbol(null,"cause","cause",1872432779,null)], null),new cljs.core.Keyword(null,"as","as",1148689641),new cljs.core.Symbol(null,"ed","ed",2076825751,null)], null)], null)),null,(cljs.core.truth_(figwheel.client.default_on_compile_fail)?figwheel.client.default_on_compile_fail.cljs$lang$test:null)])),false,true,["ws://",cljs.core.str.cljs$core$IFn$_invoke$arity$1(((figwheel.client.utils.html_env_QMARK_.call(null))?location.host:"localhost:3449")),"/figwheel-ws"].join(''),false,figwheel.client.default_before_load,false,false,(100),true,figwheel.client.default_on_cssload]);
}
figwheel.client.handle_deprecated_jsload_callback = (function figwheel$client$handle_deprecated_jsload_callback(config){
if(cljs.core.truth_(new cljs.core.Keyword(null,"jsload-callback","jsload-callback",-1949628369).cljs$core$IFn$_invoke$arity$1(config))){
return cljs.core.dissoc.call(null,cljs.core.assoc.call(null,config,new cljs.core.Keyword(null,"on-jsload","on-jsload",-395756602),new cljs.core.Keyword(null,"jsload-callback","jsload-callback",-1949628369).cljs$core$IFn$_invoke$arity$1(config)),new cljs.core.Keyword(null,"jsload-callback","jsload-callback",-1949628369));
} else {
return config;
}
});
figwheel.client.fill_url_template = (function figwheel$client$fill_url_template(config){
if(figwheel.client.utils.html_env_QMARK_.call(null)){
return cljs.core.update_in.call(null,config,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"websocket-url","websocket-url",-490444938)], null),(function (x){
return clojure.string.replace.call(null,clojure.string.replace.call(null,x,"[[client-hostname]]",location.hostname),"[[client-port]]",location.port);
}));
} else {
return config;
}
});
figwheel.client.base_plugins = (function figwheel$client$base_plugins(system_options){
var base = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"enforce-project-plugin","enforce-project-plugin",959402899),figwheel.client.enforce_project_plugin,new cljs.core.Keyword(null,"enforce-figwheel-version-plugin","enforce-figwheel-version-plugin",-1916185220),figwheel.client.enforce_figwheel_version_plugin,new cljs.core.Keyword(null,"file-reloader-plugin","file-reloader-plugin",-1792964733),figwheel.client.file_reloader_plugin,new cljs.core.Keyword(null,"comp-fail-warning-plugin","comp-fail-warning-plugin",634311),figwheel.client.compile_fail_warning_plugin,new cljs.core.Keyword(null,"css-reloader-plugin","css-reloader-plugin",2002032904),figwheel.client.css_reloader_plugin,new cljs.core.Keyword(null,"repl-plugin","repl-plugin",-1138952371),figwheel.client.repl_plugin], null);
var base__$1 = (((!(figwheel.client.utils.html_env_QMARK_.call(null))))?cljs.core.select_keys.call(null,base,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"file-reloader-plugin","file-reloader-plugin",-1792964733),new cljs.core.Keyword(null,"comp-fail-warning-plugin","comp-fail-warning-plugin",634311),new cljs.core.Keyword(null,"repl-plugin","repl-plugin",-1138952371)], null)):base);
var base__$2 = ((new cljs.core.Keyword(null,"autoload","autoload",-354122500).cljs$core$IFn$_invoke$arity$1(system_options) === false)?cljs.core.dissoc.call(null,base__$1,new cljs.core.Keyword(null,"file-reloader-plugin","file-reloader-plugin",-1792964733)):base__$1);
if(cljs.core.truth_((function (){var and__4120__auto__ = new cljs.core.Keyword(null,"heads-up-display","heads-up-display",-896577202).cljs$core$IFn$_invoke$arity$1(system_options);
if(cljs.core.truth_(and__4120__auto__)){
return figwheel.client.utils.html_env_QMARK_.call(null);
} else {
return and__4120__auto__;
}
})())){
return cljs.core.assoc.call(null,base__$2,new cljs.core.Keyword(null,"heads-up-display-plugin","heads-up-display-plugin",1745207501),figwheel.client.heads_up_plugin);
} else {
return base__$2;
}
});
figwheel.client.add_message_watch = (function figwheel$client$add_message_watch(key,callback){
return cljs.core.add_watch.call(null,figwheel.client.socket.message_history_atom,key,(function (_,___$1,___$2,msg_hist){
return callback.call(null,cljs.core.first.call(null,msg_hist));
}));
});
figwheel.client.add_json_message_watch = (function figwheel$client$add_json_message_watch(key,callback){
return figwheel.client.add_message_watch.call(null,key,cljs.core.comp.call(null,callback,cljs.core.clj__GT_js));
});
goog.exportSymbol('figwheel.client.add_json_message_watch', figwheel.client.add_json_message_watch);
figwheel.client.add_plugins = (function figwheel$client$add_plugins(plugins,system_options){
var seq__37887 = cljs.core.seq.call(null,plugins);
var chunk__37888 = null;
var count__37889 = (0);
var i__37890 = (0);
while(true){
if((i__37890 < count__37889)){
var vec__37897 = cljs.core._nth.call(null,chunk__37888,i__37890);
var k = cljs.core.nth.call(null,vec__37897,(0),null);
var plugin = cljs.core.nth.call(null,vec__37897,(1),null);
if(cljs.core.truth_(plugin)){
var pl_37903 = plugin.call(null,system_options);
cljs.core.add_watch.call(null,figwheel.client.socket.message_history_atom,k,((function (seq__37887,chunk__37888,count__37889,i__37890,pl_37903,vec__37897,k,plugin){
return (function (_,___$1,___$2,msg_hist){
return pl_37903.call(null,msg_hist);
});})(seq__37887,chunk__37888,count__37889,i__37890,pl_37903,vec__37897,k,plugin))
);
} else {
}


var G__37904 = seq__37887;
var G__37905 = chunk__37888;
var G__37906 = count__37889;
var G__37907 = (i__37890 + (1));
seq__37887 = G__37904;
chunk__37888 = G__37905;
count__37889 = G__37906;
i__37890 = G__37907;
continue;
} else {
var temp__5753__auto__ = cljs.core.seq.call(null,seq__37887);
if(temp__5753__auto__){
var seq__37887__$1 = temp__5753__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__37887__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__37887__$1);
var G__37908 = cljs.core.chunk_rest.call(null,seq__37887__$1);
var G__37909 = c__4550__auto__;
var G__37910 = cljs.core.count.call(null,c__4550__auto__);
var G__37911 = (0);
seq__37887 = G__37908;
chunk__37888 = G__37909;
count__37889 = G__37910;
i__37890 = G__37911;
continue;
} else {
var vec__37900 = cljs.core.first.call(null,seq__37887__$1);
var k = cljs.core.nth.call(null,vec__37900,(0),null);
var plugin = cljs.core.nth.call(null,vec__37900,(1),null);
if(cljs.core.truth_(plugin)){
var pl_37912 = plugin.call(null,system_options);
cljs.core.add_watch.call(null,figwheel.client.socket.message_history_atom,k,((function (seq__37887,chunk__37888,count__37889,i__37890,pl_37912,vec__37900,k,plugin,seq__37887__$1,temp__5753__auto__){
return (function (_,___$1,___$2,msg_hist){
return pl_37912.call(null,msg_hist);
});})(seq__37887,chunk__37888,count__37889,i__37890,pl_37912,vec__37900,k,plugin,seq__37887__$1,temp__5753__auto__))
);
} else {
}


var G__37913 = cljs.core.next.call(null,seq__37887__$1);
var G__37914 = null;
var G__37915 = (0);
var G__37916 = (0);
seq__37887 = G__37913;
chunk__37888 = G__37914;
count__37889 = G__37915;
i__37890 = G__37916;
continue;
}
} else {
return null;
}
}
break;
}
});
figwheel.client.start = (function figwheel$client$start(var_args){
var G__37918 = arguments.length;
switch (G__37918) {
case 1:
return figwheel.client.start.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 0:
return figwheel.client.start.cljs$core$IFn$_invoke$arity$0();

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

figwheel.client.start.cljs$core$IFn$_invoke$arity$1 = (function (opts){
if((goog.dependencies_ == null)){
return null;
} else {
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.__figwheel_start_once__ !== 'undefined')){
return null;
} else {
return (
figwheel.client.__figwheel_start_once__ = setTimeout((function (){
var plugins_SINGLEQUOTE_ = new cljs.core.Keyword(null,"plugins","plugins",1900073717).cljs$core$IFn$_invoke$arity$1(opts);
var merge_plugins = new cljs.core.Keyword(null,"merge-plugins","merge-plugins",-1193912370).cljs$core$IFn$_invoke$arity$1(opts);
var system_options = figwheel.client.fill_url_template.call(null,figwheel.client.handle_deprecated_jsload_callback.call(null,cljs.core.merge.call(null,figwheel.client.config_defaults,cljs.core.dissoc.call(null,opts,new cljs.core.Keyword(null,"plugins","plugins",1900073717),new cljs.core.Keyword(null,"merge-plugins","merge-plugins",-1193912370)))));
var plugins = (cljs.core.truth_(plugins_SINGLEQUOTE_)?plugins_SINGLEQUOTE_:cljs.core.merge.call(null,figwheel.client.base_plugins.call(null,system_options),merge_plugins));
figwheel.client.utils._STAR_print_debug_STAR_ = new cljs.core.Keyword(null,"debug","debug",-1608172596).cljs$core$IFn$_invoke$arity$1(opts);

figwheel.client.enable_repl_print_BANG_.call(null);

figwheel.client.add_plugins.call(null,plugins,system_options);

figwheel.client.file_reloading.patch_goog_base.call(null);

var seq__37919_37924 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"initial-messages","initial-messages",2057377771).cljs$core$IFn$_invoke$arity$1(system_options));
var chunk__37920_37925 = null;
var count__37921_37926 = (0);
var i__37922_37927 = (0);
while(true){
if((i__37922_37927 < count__37921_37926)){
var msg_37928 = cljs.core._nth.call(null,chunk__37920_37925,i__37922_37927);
figwheel.client.socket.handle_incoming_message.call(null,msg_37928);


var G__37929 = seq__37919_37924;
var G__37930 = chunk__37920_37925;
var G__37931 = count__37921_37926;
var G__37932 = (i__37922_37927 + (1));
seq__37919_37924 = G__37929;
chunk__37920_37925 = G__37930;
count__37921_37926 = G__37931;
i__37922_37927 = G__37932;
continue;
} else {
var temp__5753__auto___37933 = cljs.core.seq.call(null,seq__37919_37924);
if(temp__5753__auto___37933){
var seq__37919_37934__$1 = temp__5753__auto___37933;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__37919_37934__$1)){
var c__4550__auto___37935 = cljs.core.chunk_first.call(null,seq__37919_37934__$1);
var G__37936 = cljs.core.chunk_rest.call(null,seq__37919_37934__$1);
var G__37937 = c__4550__auto___37935;
var G__37938 = cljs.core.count.call(null,c__4550__auto___37935);
var G__37939 = (0);
seq__37919_37924 = G__37936;
chunk__37920_37925 = G__37937;
count__37921_37926 = G__37938;
i__37922_37927 = G__37939;
continue;
} else {
var msg_37940 = cljs.core.first.call(null,seq__37919_37934__$1);
figwheel.client.socket.handle_incoming_message.call(null,msg_37940);


var G__37941 = cljs.core.next.call(null,seq__37919_37934__$1);
var G__37942 = null;
var G__37943 = (0);
var G__37944 = (0);
seq__37919_37924 = G__37941;
chunk__37920_37925 = G__37942;
count__37921_37926 = G__37943;
i__37922_37927 = G__37944;
continue;
}
} else {
}
}
break;
}

return figwheel.client.socket.open.call(null,system_options);
})))
;
}
}
});

figwheel.client.start.cljs$core$IFn$_invoke$arity$0 = (function (){
return figwheel.client.start.call(null,cljs.core.PersistentArrayMap.EMPTY);
});

figwheel.client.start.cljs$lang$maxFixedArity = 1;

figwheel.client.watch_and_reload_with_opts = figwheel.client.start;
figwheel.client.watch_and_reload = (function figwheel$client$watch_and_reload(var_args){
var args__4736__auto__ = [];
var len__4730__auto___37949 = arguments.length;
var i__4731__auto___37950 = (0);
while(true){
if((i__4731__auto___37950 < len__4730__auto___37949)){
args__4736__auto__.push((arguments[i__4731__auto___37950]));

var G__37951 = (i__4731__auto___37950 + (1));
i__4731__auto___37950 = G__37951;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return figwheel.client.watch_and_reload.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

figwheel.client.watch_and_reload.cljs$core$IFn$_invoke$arity$variadic = (function (p__37946){
var map__37947 = p__37946;
var map__37947__$1 = (((((!((map__37947 == null))))?(((((map__37947.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37947.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37947):map__37947);
var opts = map__37947__$1;
return figwheel.client.start.call(null,opts);
});

figwheel.client.watch_and_reload.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
figwheel.client.watch_and_reload.cljs$lang$applyTo = (function (seq37945){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq37945));
});

figwheel.client.fetch_data_from_env = (function figwheel$client$fetch_data_from_env(){
try{return cljs.reader.read_string.call(null,goog.object.get(window,"FIGWHEEL_CLIENT_CONFIGURATION"));
}catch (e37952){if((e37952 instanceof Error)){
var e = e37952;
cljs.core._STAR_print_err_fn_STAR_.call(null,"Unable to load FIGWHEEL_CLIENT_CONFIGURATION from the environment");

return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"autoload","autoload",-354122500),false], null);
} else {
throw e37952;

}
}});
figwheel.client.console_intro_message = "Figwheel has compiled a temporary helper application to your :output-file.\n\nThe code currently in your configured output file does not\nrepresent the code that you are trying to compile.\n\nThis temporary application is intended to help you continue to get\nfeedback from Figwheel until the build you are working on compiles\ncorrectly.\n\nWhen your ClojureScript source code compiles correctly this helper\napplication will auto-reload and pick up your freshly compiled\nClojureScript program.";
figwheel.client.bad_compile_helper_app = (function figwheel$client$bad_compile_helper_app(){
cljs.core.enable_console_print_BANG_.call(null);

var config = figwheel.client.fetch_data_from_env.call(null);
cljs.core.println.call(null,figwheel.client.console_intro_message);

figwheel.client.heads_up.bad_compile_screen.call(null);

if(cljs.core.truth_(goog.dependencies_)){
} else {
goog.dependencies_ = true;
}

figwheel.client.start.call(null,config);

return figwheel.client.add_message_watch.call(null,new cljs.core.Keyword(null,"listen-for-successful-compile","listen-for-successful-compile",-995277603),((function (config){
return (function (p__37953){
var map__37954 = p__37953;
var map__37954__$1 = (((((!((map__37954 == null))))?(((((map__37954.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37954.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37954):map__37954);
var msg_name = cljs.core.get.call(null,map__37954__$1,new cljs.core.Keyword(null,"msg-name","msg-name",-353709863));
if(cljs.core._EQ_.call(null,msg_name,new cljs.core.Keyword(null,"files-changed","files-changed",-1418200563))){
return location.href = location.href;
} else {
return null;
}
});})(config))
);
});

//# sourceMappingURL=client.js.map?rel=1642872180569
