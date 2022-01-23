// Compiled by ClojureScript 1.10.520 {}
goog.provide('figwheel.client.file_reloading');
goog.require('cljs.core');
goog.require('figwheel.client.utils');
goog.require('goog.Uri');
goog.require('goog.string');
goog.require('goog.object');
goog.require('goog.net.jsloader');
goog.require('goog.html.legacyconversions');
goog.require('clojure.string');
goog.require('clojure.set');
goog.require('cljs.core.async');
goog.require('goog.async.Deferred');
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.figwheel_meta_pragmas !== 'undefined')){
} else {
figwheel.client.file_reloading.figwheel_meta_pragmas = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
figwheel.client.file_reloading.on_jsload_custom_event = (function figwheel$client$file_reloading$on_jsload_custom_event(url){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.js-reload",url);
});
figwheel.client.file_reloading.before_jsload_custom_event = (function figwheel$client$file_reloading$before_jsload_custom_event(files){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.before-js-reload",files);
});
figwheel.client.file_reloading.on_cssload_custom_event = (function figwheel$client$file_reloading$on_cssload_custom_event(files){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.css-reload",files);
});
figwheel.client.file_reloading.namespace_file_map_QMARK_ = (function figwheel$client$file_reloading$namespace_file_map_QMARK_(m){
var or__4131__auto__ = ((cljs.core.map_QMARK_.call(null,m)) && (typeof new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(m) === 'string') && ((((new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(m) == null)) || (typeof new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(m) === 'string'))) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(m),new cljs.core.Keyword(null,"namespace","namespace",-377510372))));
if(or__4131__auto__){
return or__4131__auto__;
} else {
cljs.core.println.call(null,"Error not namespace-file-map",cljs.core.pr_str.call(null,m));

return false;
}
});
figwheel.client.file_reloading.add_cache_buster = (function figwheel$client$file_reloading$add_cache_buster(url){

return goog.Uri.parse(url).makeUnique();
});
figwheel.client.file_reloading.name__GT_path = (function figwheel$client$file_reloading$name__GT_path(ns){

return goog.object.get(goog.dependencies_.nameToPath,ns);
});
figwheel.client.file_reloading.provided_QMARK_ = (function figwheel$client$file_reloading$provided_QMARK_(ns){
return goog.object.get(goog.dependencies_.written,figwheel.client.file_reloading.name__GT_path.call(null,ns));
});
figwheel.client.file_reloading.immutable_ns_QMARK_ = (function figwheel$client$file_reloading$immutable_ns_QMARK_(name){
var or__4131__auto__ = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["cljs.nodejs",null,"goog",null,"cljs.core",null], null), null).call(null,name);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = goog.string.startsWith("clojure.",name);
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
return goog.string.startsWith("goog.",name);
}
}
});
figwheel.client.file_reloading.get_requires = (function figwheel$client$file_reloading$get_requires(ns){
return cljs.core.set.call(null,cljs.core.filter.call(null,(function (p1__35610_SHARP_){
return cljs.core.not.call(null,figwheel.client.file_reloading.immutable_ns_QMARK_.call(null,p1__35610_SHARP_));
}),goog.object.getKeys(goog.object.get(goog.dependencies_.requires,figwheel.client.file_reloading.name__GT_path.call(null,ns)))));
});
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.dependency_data !== 'undefined')){
} else {
figwheel.client.file_reloading.dependency_data = cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"dependents","dependents",136812837),cljs.core.PersistentArrayMap.EMPTY], null));
}
figwheel.client.file_reloading.path_to_name_BANG_ = (function figwheel$client$file_reloading$path_to_name_BANG_(path,name){
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependency_data,cljs.core.update_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),path], null),cljs.core.fnil.call(null,clojure.set.union,cljs.core.PersistentHashSet.EMPTY),cljs.core.PersistentHashSet.createAsIfByAssoc([name]));
});
/**
 * Setup a path to name dependencies map.
 * That goes from path -> #{ ns-names }
 */
figwheel.client.file_reloading.setup_path__GT_name_BANG_ = (function figwheel$client$file_reloading$setup_path__GT_name_BANG_(){
var nameToPath = goog.object.filter(goog.dependencies_.nameToPath,(function (v,k,o){
return goog.string.startsWith(v,"../");
}));
return goog.object.forEach(nameToPath,((function (nameToPath){
return (function (v,k,o){
return figwheel.client.file_reloading.path_to_name_BANG_.call(null,v,k);
});})(nameToPath))
);
});
/**
 * returns a set of namespaces defined by a path
 */
figwheel.client.file_reloading.path__GT_name = (function figwheel$client$file_reloading$path__GT_name(path){
return cljs.core.get_in.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.dependency_data),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),path], null));
});
figwheel.client.file_reloading.name_to_parent_BANG_ = (function figwheel$client$file_reloading$name_to_parent_BANG_(ns,parent_ns){
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependency_data,cljs.core.update_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"dependents","dependents",136812837),ns], null),cljs.core.fnil.call(null,clojure.set.union,cljs.core.PersistentHashSet.EMPTY),cljs.core.PersistentHashSet.createAsIfByAssoc([parent_ns]));
});
/**
 * This reverses the goog.dependencies_.requires for looking up ns-dependents.
 */
figwheel.client.file_reloading.setup_ns__GT_dependents_BANG_ = (function figwheel$client$file_reloading$setup_ns__GT_dependents_BANG_(){
var requires = goog.object.filter(goog.dependencies_.requires,(function (v,k,o){
return goog.string.startsWith(k,"../");
}));
return goog.object.forEach(requires,((function (requires){
return (function (v,k,_){
return goog.object.forEach(v,((function (requires){
return (function (v_SINGLEQUOTE_,k_SINGLEQUOTE_,___$1){
var seq__35611 = cljs.core.seq.call(null,figwheel.client.file_reloading.path__GT_name.call(null,k));
var chunk__35612 = null;
var count__35613 = (0);
var i__35614 = (0);
while(true){
if((i__35614 < count__35613)){
var n = cljs.core._nth.call(null,chunk__35612,i__35614);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,k_SINGLEQUOTE_,n);


var G__35615 = seq__35611;
var G__35616 = chunk__35612;
var G__35617 = count__35613;
var G__35618 = (i__35614 + (1));
seq__35611 = G__35615;
chunk__35612 = G__35616;
count__35613 = G__35617;
i__35614 = G__35618;
continue;
} else {
var temp__5753__auto__ = cljs.core.seq.call(null,seq__35611);
if(temp__5753__auto__){
var seq__35611__$1 = temp__5753__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35611__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__35611__$1);
var G__35619 = cljs.core.chunk_rest.call(null,seq__35611__$1);
var G__35620 = c__4550__auto__;
var G__35621 = cljs.core.count.call(null,c__4550__auto__);
var G__35622 = (0);
seq__35611 = G__35619;
chunk__35612 = G__35620;
count__35613 = G__35621;
i__35614 = G__35622;
continue;
} else {
var n = cljs.core.first.call(null,seq__35611__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,k_SINGLEQUOTE_,n);


var G__35623 = cljs.core.next.call(null,seq__35611__$1);
var G__35624 = null;
var G__35625 = (0);
var G__35626 = (0);
seq__35611 = G__35623;
chunk__35612 = G__35624;
count__35613 = G__35625;
i__35614 = G__35626;
continue;
}
} else {
return null;
}
}
break;
}
});})(requires))
);
});})(requires))
);
});
figwheel.client.file_reloading.ns__GT_dependents = (function figwheel$client$file_reloading$ns__GT_dependents(ns){
return cljs.core.get_in.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.dependency_data),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"dependents","dependents",136812837),ns], null));
});
figwheel.client.file_reloading.in_upper_level_QMARK_ = (function figwheel$client$file_reloading$in_upper_level_QMARK_(topo_state,current_depth,dep){
return cljs.core.some.call(null,(function (p__35627){
var vec__35628 = p__35627;
var _ = cljs.core.nth.call(null,vec__35628,(0),null);
var v = cljs.core.nth.call(null,vec__35628,(1),null);
var and__4120__auto__ = v;
if(cljs.core.truth_(and__4120__auto__)){
return v.call(null,dep);
} else {
return and__4120__auto__;
}
}),cljs.core.filter.call(null,(function (p__35631){
var vec__35632 = p__35631;
var k = cljs.core.nth.call(null,vec__35632,(0),null);
var v = cljs.core.nth.call(null,vec__35632,(1),null);
return (k > current_depth);
}),topo_state));
});
figwheel.client.file_reloading.build_topo_sort = (function figwheel$client$file_reloading$build_topo_sort(get_deps){
var get_deps__$1 = cljs.core.memoize.call(null,get_deps);
var topo_sort_helper_STAR_ = ((function (get_deps__$1){
return (function figwheel$client$file_reloading$build_topo_sort_$_topo_sort_helper_STAR_(x,depth,state){
var deps = get_deps__$1.call(null,x);
if(cljs.core.empty_QMARK_.call(null,deps)){
return null;
} else {
return topo_sort_STAR_.call(null,deps,depth,state);
}
});})(get_deps__$1))
;
var topo_sort_STAR_ = ((function (get_deps__$1){
return (function() {
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_ = null;
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1 = (function (deps){
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.call(null,deps,(0),cljs.core.atom.call(null,cljs.core.sorted_map.call(null)));
});
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3 = (function (deps,depth,state){
cljs.core.swap_BANG_.call(null,state,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [depth], null),cljs.core.fnil.call(null,cljs.core.into,cljs.core.PersistentHashSet.EMPTY),deps);

var seq__35644_35652 = cljs.core.seq.call(null,deps);
var chunk__35645_35653 = null;
var count__35646_35654 = (0);
var i__35647_35655 = (0);
while(true){
if((i__35647_35655 < count__35646_35654)){
var dep_35656 = cljs.core._nth.call(null,chunk__35645_35653,i__35647_35655);
if(cljs.core.truth_((function (){var and__4120__auto__ = dep_35656;
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core.not.call(null,figwheel.client.file_reloading.in_upper_level_QMARK_.call(null,cljs.core.deref.call(null,state),depth,dep_35656));
} else {
return and__4120__auto__;
}
})())){
topo_sort_helper_STAR_.call(null,dep_35656,(depth + (1)),state);
} else {
}


var G__35657 = seq__35644_35652;
var G__35658 = chunk__35645_35653;
var G__35659 = count__35646_35654;
var G__35660 = (i__35647_35655 + (1));
seq__35644_35652 = G__35657;
chunk__35645_35653 = G__35658;
count__35646_35654 = G__35659;
i__35647_35655 = G__35660;
continue;
} else {
var temp__5753__auto___35661 = cljs.core.seq.call(null,seq__35644_35652);
if(temp__5753__auto___35661){
var seq__35644_35662__$1 = temp__5753__auto___35661;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35644_35662__$1)){
var c__4550__auto___35663 = cljs.core.chunk_first.call(null,seq__35644_35662__$1);
var G__35664 = cljs.core.chunk_rest.call(null,seq__35644_35662__$1);
var G__35665 = c__4550__auto___35663;
var G__35666 = cljs.core.count.call(null,c__4550__auto___35663);
var G__35667 = (0);
seq__35644_35652 = G__35664;
chunk__35645_35653 = G__35665;
count__35646_35654 = G__35666;
i__35647_35655 = G__35667;
continue;
} else {
var dep_35668 = cljs.core.first.call(null,seq__35644_35662__$1);
if(cljs.core.truth_((function (){var and__4120__auto__ = dep_35668;
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core.not.call(null,figwheel.client.file_reloading.in_upper_level_QMARK_.call(null,cljs.core.deref.call(null,state),depth,dep_35668));
} else {
return and__4120__auto__;
}
})())){
topo_sort_helper_STAR_.call(null,dep_35668,(depth + (1)),state);
} else {
}


var G__35669 = cljs.core.next.call(null,seq__35644_35662__$1);
var G__35670 = null;
var G__35671 = (0);
var G__35672 = (0);
seq__35644_35652 = G__35669;
chunk__35645_35653 = G__35670;
count__35646_35654 = G__35671;
i__35647_35655 = G__35672;
continue;
}
} else {
}
}
break;
}

if(cljs.core._EQ_.call(null,depth,(0))){
return elim_dups_STAR_.call(null,cljs.core.reverse.call(null,cljs.core.vals.call(null,cljs.core.deref.call(null,state))));
} else {
return null;
}
});
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_ = function(deps,depth,state){
switch(arguments.length){
case 1:
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1.call(this,deps);
case 3:
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3.call(this,deps,depth,state);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1;
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.cljs$core$IFn$_invoke$arity$3 = figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3;
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_;
})()
;})(get_deps__$1))
;
var elim_dups_STAR_ = ((function (get_deps__$1){
return (function figwheel$client$file_reloading$build_topo_sort_$_elim_dups_STAR_(p__35648){
var vec__35649 = p__35648;
var seq__35650 = cljs.core.seq.call(null,vec__35649);
var first__35651 = cljs.core.first.call(null,seq__35650);
var seq__35650__$1 = cljs.core.next.call(null,seq__35650);
var x = first__35651;
var xs = seq__35650__$1;
if((x == null)){
return cljs.core.List.EMPTY;
} else {
return cljs.core.cons.call(null,x,figwheel$client$file_reloading$build_topo_sort_$_elim_dups_STAR_.call(null,cljs.core.map.call(null,((function (vec__35649,seq__35650,first__35651,seq__35650__$1,x,xs,get_deps__$1){
return (function (p1__35635_SHARP_){
return clojure.set.difference.call(null,p1__35635_SHARP_,x);
});})(vec__35649,seq__35650,first__35651,seq__35650__$1,x,xs,get_deps__$1))
,xs)));
}
});})(get_deps__$1))
;
return topo_sort_STAR_;
});
figwheel.client.file_reloading.get_all_dependencies = (function figwheel$client$file_reloading$get_all_dependencies(ns){
var topo_sort_SINGLEQUOTE_ = figwheel.client.file_reloading.build_topo_sort.call(null,figwheel.client.file_reloading.get_requires);
return cljs.core.apply.call(null,cljs.core.concat,topo_sort_SINGLEQUOTE_.call(null,cljs.core.set.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ns], null))));
});
figwheel.client.file_reloading.get_all_dependents = (function figwheel$client$file_reloading$get_all_dependents(nss){
var topo_sort_SINGLEQUOTE_ = figwheel.client.file_reloading.build_topo_sort.call(null,figwheel.client.file_reloading.ns__GT_dependents);
return cljs.core.filter.call(null,cljs.core.comp.call(null,cljs.core.not,figwheel.client.file_reloading.immutable_ns_QMARK_),cljs.core.reverse.call(null,cljs.core.apply.call(null,cljs.core.concat,topo_sort_SINGLEQUOTE_.call(null,cljs.core.set.call(null,nss)))));
});
figwheel.client.file_reloading.unprovide_BANG_ = (function figwheel$client$file_reloading$unprovide_BANG_(ns){
var path = figwheel.client.file_reloading.name__GT_path.call(null,ns);
goog.object.remove(goog.dependencies_.visited,path);

goog.object.remove(goog.dependencies_.written,path);

return goog.object.remove(goog.dependencies_.written,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.basePath),cljs.core.str.cljs$core$IFn$_invoke$arity$1(path)].join(''));
});
figwheel.client.file_reloading.resolve_ns = (function figwheel$client$file_reloading$resolve_ns(ns){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.basePath),cljs.core.str.cljs$core$IFn$_invoke$arity$1(figwheel.client.file_reloading.name__GT_path.call(null,ns))].join('');
});
figwheel.client.file_reloading.addDependency = (function figwheel$client$file_reloading$addDependency(path,provides,requires){
var seq__35673 = cljs.core.seq.call(null,provides);
var chunk__35674 = null;
var count__35675 = (0);
var i__35676 = (0);
while(true){
if((i__35676 < count__35675)){
var prov = cljs.core._nth.call(null,chunk__35674,i__35676);
figwheel.client.file_reloading.path_to_name_BANG_.call(null,path,prov);

var seq__35685_35693 = cljs.core.seq.call(null,requires);
var chunk__35686_35694 = null;
var count__35687_35695 = (0);
var i__35688_35696 = (0);
while(true){
if((i__35688_35696 < count__35687_35695)){
var req_35697 = cljs.core._nth.call(null,chunk__35686_35694,i__35688_35696);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_35697,prov);


var G__35698 = seq__35685_35693;
var G__35699 = chunk__35686_35694;
var G__35700 = count__35687_35695;
var G__35701 = (i__35688_35696 + (1));
seq__35685_35693 = G__35698;
chunk__35686_35694 = G__35699;
count__35687_35695 = G__35700;
i__35688_35696 = G__35701;
continue;
} else {
var temp__5753__auto___35702 = cljs.core.seq.call(null,seq__35685_35693);
if(temp__5753__auto___35702){
var seq__35685_35703__$1 = temp__5753__auto___35702;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35685_35703__$1)){
var c__4550__auto___35704 = cljs.core.chunk_first.call(null,seq__35685_35703__$1);
var G__35705 = cljs.core.chunk_rest.call(null,seq__35685_35703__$1);
var G__35706 = c__4550__auto___35704;
var G__35707 = cljs.core.count.call(null,c__4550__auto___35704);
var G__35708 = (0);
seq__35685_35693 = G__35705;
chunk__35686_35694 = G__35706;
count__35687_35695 = G__35707;
i__35688_35696 = G__35708;
continue;
} else {
var req_35709 = cljs.core.first.call(null,seq__35685_35703__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_35709,prov);


var G__35710 = cljs.core.next.call(null,seq__35685_35703__$1);
var G__35711 = null;
var G__35712 = (0);
var G__35713 = (0);
seq__35685_35693 = G__35710;
chunk__35686_35694 = G__35711;
count__35687_35695 = G__35712;
i__35688_35696 = G__35713;
continue;
}
} else {
}
}
break;
}


var G__35714 = seq__35673;
var G__35715 = chunk__35674;
var G__35716 = count__35675;
var G__35717 = (i__35676 + (1));
seq__35673 = G__35714;
chunk__35674 = G__35715;
count__35675 = G__35716;
i__35676 = G__35717;
continue;
} else {
var temp__5753__auto__ = cljs.core.seq.call(null,seq__35673);
if(temp__5753__auto__){
var seq__35673__$1 = temp__5753__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35673__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__35673__$1);
var G__35718 = cljs.core.chunk_rest.call(null,seq__35673__$1);
var G__35719 = c__4550__auto__;
var G__35720 = cljs.core.count.call(null,c__4550__auto__);
var G__35721 = (0);
seq__35673 = G__35718;
chunk__35674 = G__35719;
count__35675 = G__35720;
i__35676 = G__35721;
continue;
} else {
var prov = cljs.core.first.call(null,seq__35673__$1);
figwheel.client.file_reloading.path_to_name_BANG_.call(null,path,prov);

var seq__35689_35722 = cljs.core.seq.call(null,requires);
var chunk__35690_35723 = null;
var count__35691_35724 = (0);
var i__35692_35725 = (0);
while(true){
if((i__35692_35725 < count__35691_35724)){
var req_35726 = cljs.core._nth.call(null,chunk__35690_35723,i__35692_35725);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_35726,prov);


var G__35727 = seq__35689_35722;
var G__35728 = chunk__35690_35723;
var G__35729 = count__35691_35724;
var G__35730 = (i__35692_35725 + (1));
seq__35689_35722 = G__35727;
chunk__35690_35723 = G__35728;
count__35691_35724 = G__35729;
i__35692_35725 = G__35730;
continue;
} else {
var temp__5753__auto___35731__$1 = cljs.core.seq.call(null,seq__35689_35722);
if(temp__5753__auto___35731__$1){
var seq__35689_35732__$1 = temp__5753__auto___35731__$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35689_35732__$1)){
var c__4550__auto___35733 = cljs.core.chunk_first.call(null,seq__35689_35732__$1);
var G__35734 = cljs.core.chunk_rest.call(null,seq__35689_35732__$1);
var G__35735 = c__4550__auto___35733;
var G__35736 = cljs.core.count.call(null,c__4550__auto___35733);
var G__35737 = (0);
seq__35689_35722 = G__35734;
chunk__35690_35723 = G__35735;
count__35691_35724 = G__35736;
i__35692_35725 = G__35737;
continue;
} else {
var req_35738 = cljs.core.first.call(null,seq__35689_35732__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_35738,prov);


var G__35739 = cljs.core.next.call(null,seq__35689_35732__$1);
var G__35740 = null;
var G__35741 = (0);
var G__35742 = (0);
seq__35689_35722 = G__35739;
chunk__35690_35723 = G__35740;
count__35691_35724 = G__35741;
i__35692_35725 = G__35742;
continue;
}
} else {
}
}
break;
}


var G__35743 = cljs.core.next.call(null,seq__35673__$1);
var G__35744 = null;
var G__35745 = (0);
var G__35746 = (0);
seq__35673 = G__35743;
chunk__35674 = G__35744;
count__35675 = G__35745;
i__35676 = G__35746;
continue;
}
} else {
return null;
}
}
break;
}
});
figwheel.client.file_reloading.figwheel_require = (function figwheel$client$file_reloading$figwheel_require(src,reload){
goog.require = figwheel.client.file_reloading.figwheel_require;

if(cljs.core._EQ_.call(null,reload,"reload-all")){
var seq__35747_35751 = cljs.core.seq.call(null,figwheel.client.file_reloading.get_all_dependencies.call(null,src));
var chunk__35748_35752 = null;
var count__35749_35753 = (0);
var i__35750_35754 = (0);
while(true){
if((i__35750_35754 < count__35749_35753)){
var ns_35755 = cljs.core._nth.call(null,chunk__35748_35752,i__35750_35754);
figwheel.client.file_reloading.unprovide_BANG_.call(null,ns_35755);


var G__35756 = seq__35747_35751;
var G__35757 = chunk__35748_35752;
var G__35758 = count__35749_35753;
var G__35759 = (i__35750_35754 + (1));
seq__35747_35751 = G__35756;
chunk__35748_35752 = G__35757;
count__35749_35753 = G__35758;
i__35750_35754 = G__35759;
continue;
} else {
var temp__5753__auto___35760 = cljs.core.seq.call(null,seq__35747_35751);
if(temp__5753__auto___35760){
var seq__35747_35761__$1 = temp__5753__auto___35760;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__35747_35761__$1)){
var c__4550__auto___35762 = cljs.core.chunk_first.call(null,seq__35747_35761__$1);
var G__35763 = cljs.core.chunk_rest.call(null,seq__35747_35761__$1);
var G__35764 = c__4550__auto___35762;
var G__35765 = cljs.core.count.call(null,c__4550__auto___35762);
var G__35766 = (0);
seq__35747_35751 = G__35763;
chunk__35748_35752 = G__35764;
count__35749_35753 = G__35765;
i__35750_35754 = G__35766;
continue;
} else {
var ns_35767 = cljs.core.first.call(null,seq__35747_35761__$1);
figwheel.client.file_reloading.unprovide_BANG_.call(null,ns_35767);


var G__35768 = cljs.core.next.call(null,seq__35747_35761__$1);
var G__35769 = null;
var G__35770 = (0);
var G__35771 = (0);
seq__35747_35751 = G__35768;
chunk__35748_35752 = G__35769;
count__35749_35753 = G__35770;
i__35750_35754 = G__35771;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(reload)){
figwheel.client.file_reloading.unprovide_BANG_.call(null,src);
} else {
}

return goog.require_figwheel_backup_(src);
});
/**
 * Reusable browser REPL bootstrapping. Patches the essential functions
 *   in goog.base to support re-loading of namespaces after page load.
 */
figwheel.client.file_reloading.bootstrap_goog_base = (function figwheel$client$file_reloading$bootstrap_goog_base(){
if(cljs.core.truth_(COMPILED)){
return null;
} else {
goog.require_figwheel_backup_ = (function (){var or__4131__auto__ = goog.require__;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return goog.require;
}
})();

goog.isProvided_ = (function (name){
return false;
});

figwheel.client.file_reloading.setup_path__GT_name_BANG_.call(null);

figwheel.client.file_reloading.setup_ns__GT_dependents_BANG_.call(null);

goog.addDependency_figwheel_backup_ = goog.addDependency;

goog.addDependency = (function() { 
var G__35772__delegate = function (args){
cljs.core.apply.call(null,figwheel.client.file_reloading.addDependency,args);

return cljs.core.apply.call(null,goog.addDependency_figwheel_backup_,args);
};
var G__35772 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__35773__i = 0, G__35773__a = new Array(arguments.length -  0);
while (G__35773__i < G__35773__a.length) {G__35773__a[G__35773__i] = arguments[G__35773__i + 0]; ++G__35773__i;}
  args = new cljs.core.IndexedSeq(G__35773__a,0,null);
} 
return G__35772__delegate.call(this,args);};
G__35772.cljs$lang$maxFixedArity = 0;
G__35772.cljs$lang$applyTo = (function (arglist__35774){
var args = cljs.core.seq(arglist__35774);
return G__35772__delegate(args);
});
G__35772.cljs$core$IFn$_invoke$arity$variadic = G__35772__delegate;
return G__35772;
})()
;

goog.constructNamespace_("cljs.user");

goog.global.CLOSURE_IMPORT_SCRIPT = figwheel.client.file_reloading.queued_file_reload;

return goog.require = figwheel.client.file_reloading.figwheel_require;
}
});
figwheel.client.file_reloading.patch_goog_base = (function figwheel$client$file_reloading$patch_goog_base(){
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.bootstrapped_cljs !== 'undefined')){
return null;
} else {
return (
figwheel.client.file_reloading.bootstrapped_cljs = (function (){
figwheel.client.file_reloading.bootstrap_goog_base.call(null);

return true;
})()
)
;
}
});
figwheel.client.file_reloading.gloader = (((typeof goog !== 'undefined') && (typeof goog.net !== 'undefined') && (typeof goog.net.jsloader !== 'undefined') && (typeof goog.net.jsloader.safeLoad !== 'undefined'))?(function (p1__35775_SHARP_,p2__35776_SHARP_){
return goog.net.jsloader.safeLoad(goog.html.legacyconversions.trustedResourceUrlFromString(cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__35775_SHARP_)),p2__35776_SHARP_);
}):(((typeof goog !== 'undefined') && (typeof goog.net !== 'undefined') && (typeof goog.net.jsloader !== 'undefined') && (typeof goog.net.jsloader.load !== 'undefined'))?(function (p1__35777_SHARP_,p2__35778_SHARP_){
return goog.net.jsloader.load(cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__35777_SHARP_),p2__35778_SHARP_);
}):(function(){throw cljs.core.ex_info.call(null,"No remote script loading function found.",cljs.core.PersistentArrayMap.EMPTY)})()
));
figwheel.client.file_reloading.reload_file_in_html_env = (function figwheel$client$file_reloading$reload_file_in_html_env(request_url,callback){

var G__35779 = figwheel.client.file_reloading.gloader.call(null,figwheel.client.file_reloading.add_cache_buster.call(null,request_url),({"cleanupWhenDone": true}));
G__35779.addCallback(((function (G__35779){
return (function (){
return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [true], null));
});})(G__35779))
);

G__35779.addErrback(((function (G__35779){
return (function (){
return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [false], null));
});})(G__35779))
);

return G__35779;
});
figwheel.client.file_reloading.write_script_tag_import = figwheel.client.file_reloading.reload_file_in_html_env;
goog.exportSymbol('figwheel.client.file_reloading.write_script_tag_import', figwheel.client.file_reloading.write_script_tag_import);
figwheel.client.file_reloading.worker_import_script = (function figwheel$client$file_reloading$worker_import_script(request_url,callback){

return callback.call(null,(function (){try{self.importScripts(figwheel.client.file_reloading.add_cache_buster.call(null,request_url));

return true;
}catch (e35780){if((e35780 instanceof Error)){
var e = e35780;
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),e.stack);

return false;
} else {
throw e35780;

}
}})());
});
goog.exportSymbol('figwheel.client.file_reloading.worker_import_script', figwheel.client.file_reloading.worker_import_script);
figwheel.client.file_reloading.create_node_script_import_fn = (function figwheel$client$file_reloading$create_node_script_import_fn(){
var node_path_lib = require("path");
var util_pattern = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(node_path_lib.sep),cljs.core.str.cljs$core$IFn$_invoke$arity$1(node_path_lib.join("goog","bootstrap","nodejs.js"))].join('');
var util_path = goog.object.findKey(require.cache,((function (node_path_lib,util_pattern){
return (function (v,k,o){
return goog.string.endsWith(k,util_pattern);
});})(node_path_lib,util_pattern))
);
var parts = cljs.core.pop.call(null,cljs.core.pop.call(null,clojure.string.split.call(null,util_path,/[\/\\]/)));
var root_path = clojure.string.join.call(null,node_path_lib.sep,parts);
return ((function (node_path_lib,util_pattern,util_path,parts,root_path){
return (function (request_url,callback){

var cache_path = node_path_lib.resolve(root_path,request_url);
goog.object.remove(require.cache,cache_path);

return callback.call(null,(function (){try{return require(cache_path);
}catch (e35781){if((e35781 instanceof Error)){
var e = e35781;
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cache_path)].join(''));

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),e.stack);

return false;
} else {
throw e35781;

}
}})());
});
;})(node_path_lib,util_pattern,util_path,parts,root_path))
});
goog.exportSymbol('figwheel.client.file_reloading.create_node_script_import_fn', figwheel.client.file_reloading.create_node_script_import_fn);
figwheel.client.file_reloading.reload_file_STAR_ = (function (){var pred__35782 = cljs.core._EQ_;
var expr__35783 = figwheel.client.utils.host_env_QMARK_.call(null);
if(cljs.core.truth_(pred__35782.call(null,new cljs.core.Keyword(null,"node","node",581201198),expr__35783))){
return figwheel.client.file_reloading.create_node_script_import_fn.call(null);
} else {
if(cljs.core.truth_(pred__35782.call(null,new cljs.core.Keyword(null,"html","html",-998796897),expr__35783))){
return figwheel.client.file_reloading.write_script_tag_import;
} else {
if(cljs.core.truth_(pred__35782.call(null,new cljs.core.Keyword(null,"worker","worker",938239996),expr__35783))){
return figwheel.client.file_reloading.worker_import_script;
} else {
return ((function (pred__35782,expr__35783){
return (function (a,b){
throw "Reload not defined for this platform";
});
;})(pred__35782,expr__35783))
}
}
}
})();
figwheel.client.file_reloading.reload_file = (function figwheel$client$file_reloading$reload_file(p__35785,callback){
var map__35786 = p__35785;
var map__35786__$1 = (((((!((map__35786 == null))))?(((((map__35786.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35786.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35786):map__35786);
var file_msg = map__35786__$1;
var request_url = cljs.core.get.call(null,map__35786__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));

figwheel.client.utils.debug_prn.call(null,["FigWheel: Attempting to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return (function (){var or__4131__auto__ = goog.object.get(goog.global,"FIGWHEEL_IMPORT_SCRIPT");
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return figwheel.client.file_reloading.reload_file_STAR_;
}
})().call(null,request_url,((function (map__35786,map__35786__$1,file_msg,request_url){
return (function (success_QMARK_){
if(cljs.core.truth_(success_QMARK_)){
figwheel.client.utils.debug_prn.call(null,["FigWheel: Successfully loaded ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.assoc.call(null,file_msg,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),true)], null));
} else {
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [file_msg], null));
}
});})(map__35786,map__35786__$1,file_msg,request_url))
);
});
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.reload_chan !== 'undefined')){
} else {
figwheel.client.file_reloading.reload_chan = cljs.core.async.chan.call(null);
}
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.on_load_callbacks !== 'undefined')){
} else {
figwheel.client.file_reloading.on_load_callbacks = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.dependencies_loaded !== 'undefined')){
} else {
figwheel.client.file_reloading.dependencies_loaded = cljs.core.atom.call(null,cljs.core.PersistentVector.EMPTY);
}
figwheel.client.file_reloading.blocking_load = (function figwheel$client$file_reloading$blocking_load(url){
var out = cljs.core.async.chan.call(null);
figwheel.client.file_reloading.reload_file.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"request-url","request-url",2100346596),url], null),((function (out){
return (function (file_msg){
cljs.core.async.put_BANG_.call(null,out,file_msg);

return cljs.core.async.close_BANG_.call(null,out);
});})(out))
);

return out;
});
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.reloader_loop !== 'undefined')){
} else {
figwheel.client.file_reloading.reloader_loop = (function (){var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__){
return (function (state_35824){
var state_val_35825 = (state_35824[(1)]);
if((state_val_35825 === (7))){
var inst_35820 = (state_35824[(2)]);
var state_35824__$1 = state_35824;
var statearr_35826_35852 = state_35824__$1;
(statearr_35826_35852[(2)] = inst_35820);

(statearr_35826_35852[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (1))){
var state_35824__$1 = state_35824;
var statearr_35827_35853 = state_35824__$1;
(statearr_35827_35853[(2)] = null);

(statearr_35827_35853[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (4))){
var inst_35790 = (state_35824[(7)]);
var inst_35790__$1 = (state_35824[(2)]);
var state_35824__$1 = (function (){var statearr_35828 = state_35824;
(statearr_35828[(7)] = inst_35790__$1);

return statearr_35828;
})();
if(cljs.core.truth_(inst_35790__$1)){
var statearr_35829_35854 = state_35824__$1;
(statearr_35829_35854[(1)] = (5));

} else {
var statearr_35830_35855 = state_35824__$1;
(statearr_35830_35855[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (15))){
var inst_35803 = (state_35824[(8)]);
var inst_35805 = (state_35824[(9)]);
var inst_35807 = inst_35805.call(null,inst_35803);
var state_35824__$1 = state_35824;
var statearr_35831_35856 = state_35824__$1;
(statearr_35831_35856[(2)] = inst_35807);

(statearr_35831_35856[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (13))){
var inst_35814 = (state_35824[(2)]);
var state_35824__$1 = state_35824;
var statearr_35832_35857 = state_35824__$1;
(statearr_35832_35857[(2)] = inst_35814);

(statearr_35832_35857[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (6))){
var state_35824__$1 = state_35824;
var statearr_35833_35858 = state_35824__$1;
(statearr_35833_35858[(2)] = null);

(statearr_35833_35858[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (17))){
var inst_35811 = (state_35824[(2)]);
var state_35824__$1 = state_35824;
var statearr_35834_35859 = state_35824__$1;
(statearr_35834_35859[(2)] = inst_35811);

(statearr_35834_35859[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (3))){
var inst_35822 = (state_35824[(2)]);
var state_35824__$1 = state_35824;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35824__$1,inst_35822);
} else {
if((state_val_35825 === (12))){
var state_35824__$1 = state_35824;
var statearr_35835_35860 = state_35824__$1;
(statearr_35835_35860[(2)] = null);

(statearr_35835_35860[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (2))){
var state_35824__$1 = state_35824;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35824__$1,(4),figwheel.client.file_reloading.reload_chan);
} else {
if((state_val_35825 === (11))){
var inst_35795 = (state_35824[(10)]);
var inst_35801 = figwheel.client.file_reloading.blocking_load.call(null,inst_35795);
var state_35824__$1 = state_35824;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35824__$1,(14),inst_35801);
} else {
if((state_val_35825 === (9))){
var inst_35795 = (state_35824[(10)]);
var state_35824__$1 = state_35824;
if(cljs.core.truth_(inst_35795)){
var statearr_35836_35861 = state_35824__$1;
(statearr_35836_35861[(1)] = (11));

} else {
var statearr_35837_35862 = state_35824__$1;
(statearr_35837_35862[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (5))){
var inst_35790 = (state_35824[(7)]);
var inst_35796 = (state_35824[(11)]);
var inst_35795 = cljs.core.nth.call(null,inst_35790,(0),null);
var inst_35796__$1 = cljs.core.nth.call(null,inst_35790,(1),null);
var state_35824__$1 = (function (){var statearr_35838 = state_35824;
(statearr_35838[(11)] = inst_35796__$1);

(statearr_35838[(10)] = inst_35795);

return statearr_35838;
})();
if(cljs.core.truth_(inst_35796__$1)){
var statearr_35839_35863 = state_35824__$1;
(statearr_35839_35863[(1)] = (8));

} else {
var statearr_35840_35864 = state_35824__$1;
(statearr_35840_35864[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (14))){
var inst_35805 = (state_35824[(9)]);
var inst_35795 = (state_35824[(10)]);
var inst_35803 = (state_35824[(2)]);
var inst_35804 = cljs.core.deref.call(null,figwheel.client.file_reloading.on_load_callbacks);
var inst_35805__$1 = cljs.core.get.call(null,inst_35804,inst_35795);
var state_35824__$1 = (function (){var statearr_35841 = state_35824;
(statearr_35841[(8)] = inst_35803);

(statearr_35841[(9)] = inst_35805__$1);

return statearr_35841;
})();
if(cljs.core.truth_(inst_35805__$1)){
var statearr_35842_35865 = state_35824__$1;
(statearr_35842_35865[(1)] = (15));

} else {
var statearr_35843_35866 = state_35824__$1;
(statearr_35843_35866[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (16))){
var inst_35803 = (state_35824[(8)]);
var inst_35809 = cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependencies_loaded,cljs.core.conj,inst_35803);
var state_35824__$1 = state_35824;
var statearr_35844_35867 = state_35824__$1;
(statearr_35844_35867[(2)] = inst_35809);

(statearr_35844_35867[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (10))){
var inst_35816 = (state_35824[(2)]);
var state_35824__$1 = (function (){var statearr_35845 = state_35824;
(statearr_35845[(12)] = inst_35816);

return statearr_35845;
})();
var statearr_35846_35868 = state_35824__$1;
(statearr_35846_35868[(2)] = null);

(statearr_35846_35868[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35825 === (8))){
var inst_35796 = (state_35824[(11)]);
var inst_35798 = eval(inst_35796);
var state_35824__$1 = state_35824;
var statearr_35847_35869 = state_35824__$1;
(statearr_35847_35869[(2)] = inst_35798);

(statearr_35847_35869[(1)] = (10));


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
});})(c__33495__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__){
return (function() {
var figwheel$client$file_reloading$state_machine__33351__auto__ = null;
var figwheel$client$file_reloading$state_machine__33351__auto____0 = (function (){
var statearr_35848 = [null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_35848[(0)] = figwheel$client$file_reloading$state_machine__33351__auto__);

(statearr_35848[(1)] = (1));

return statearr_35848;
});
var figwheel$client$file_reloading$state_machine__33351__auto____1 = (function (state_35824){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35824);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35849){if((e35849 instanceof Object)){
var ex__33354__auto__ = e35849;
var statearr_35850_35870 = state_35824;
(statearr_35850_35870[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35824);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35849;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35871 = state_35824;
state_35824 = G__35871;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$file_reloading$state_machine__33351__auto__ = function(state_35824){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$state_machine__33351__auto____1.call(this,state_35824);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$file_reloading$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$state_machine__33351__auto____0;
figwheel$client$file_reloading$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$state_machine__33351__auto____1;
return figwheel$client$file_reloading$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__))
})();
var state__33497__auto__ = (function (){var statearr_35851 = f__33496__auto__.call(null);
(statearr_35851[(6)] = c__33495__auto__);

return statearr_35851;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__))
);

return c__33495__auto__;
})();
}
figwheel.client.file_reloading.queued_file_reload = (function figwheel$client$file_reloading$queued_file_reload(var_args){
var G__35873 = arguments.length;
switch (G__35873) {
case 1:
return figwheel.client.file_reloading.queued_file_reload.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return figwheel.client.file_reloading.queued_file_reload.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

figwheel.client.file_reloading.queued_file_reload.cljs$core$IFn$_invoke$arity$1 = (function (url){
return figwheel.client.file_reloading.queued_file_reload.call(null,url,null);
});

figwheel.client.file_reloading.queued_file_reload.cljs$core$IFn$_invoke$arity$2 = (function (url,opt_source_text){
return cljs.core.async.put_BANG_.call(null,figwheel.client.file_reloading.reload_chan,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [url,opt_source_text], null));
});

figwheel.client.file_reloading.queued_file_reload.cljs$lang$maxFixedArity = 2;

figwheel.client.file_reloading.require_with_callback = (function figwheel$client$file_reloading$require_with_callback(p__35875,callback){
var map__35876 = p__35875;
var map__35876__$1 = (((((!((map__35876 == null))))?(((((map__35876.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35876.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35876):map__35876);
var file_msg = map__35876__$1;
var namespace = cljs.core.get.call(null,map__35876__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var request_url = figwheel.client.file_reloading.resolve_ns.call(null,namespace);
cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.on_load_callbacks,cljs.core.assoc,request_url,((function (request_url,map__35876,map__35876__$1,file_msg,namespace){
return (function (file_msg_SINGLEQUOTE_){
cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.on_load_callbacks,cljs.core.dissoc,request_url);

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.merge.call(null,file_msg,cljs.core.select_keys.call(null,file_msg_SINGLEQUOTE_,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375)], null)))], null));
});})(request_url,map__35876,map__35876__$1,file_msg,namespace))
);

return figwheel.client.file_reloading.figwheel_require.call(null,cljs.core.name.call(null,namespace),true);
});
figwheel.client.file_reloading.figwheel_no_load_QMARK_ = (function figwheel$client$file_reloading$figwheel_no_load_QMARK_(p__35878){
var map__35879 = p__35878;
var map__35879__$1 = (((((!((map__35879 == null))))?(((((map__35879.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35879.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35879):map__35879);
var file_msg = map__35879__$1;
var namespace = cljs.core.get.call(null,map__35879__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var meta_pragmas = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
return new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
});
figwheel.client.file_reloading.ns_exists_QMARK_ = (function figwheel$client$file_reloading$ns_exists_QMARK_(namespace){
return (!((cljs.core.reduce.call(null,cljs.core.fnil.call(null,goog.object.get,({})),goog.global,clojure.string.split.call(null,cljs.core.name.call(null,namespace),".")) == null)));
});
figwheel.client.file_reloading.reload_file_QMARK_ = (function figwheel$client$file_reloading$reload_file_QMARK_(p__35881){
var map__35882 = p__35881;
var map__35882__$1 = (((((!((map__35882 == null))))?(((((map__35882.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35882.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35882):map__35882);
var file_msg = map__35882__$1;
var namespace = cljs.core.get.call(null,map__35882__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));

var meta_pragmas = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
var and__4120__auto__ = cljs.core.not.call(null,figwheel.client.file_reloading.figwheel_no_load_QMARK_.call(null,file_msg));
if(and__4120__auto__){
var or__4131__auto__ = new cljs.core.Keyword(null,"figwheel-always","figwheel-always",799819691).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = new cljs.core.Keyword(null,"figwheel-load","figwheel-load",1316089175).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
var or__4131__auto____$2 = figwheel.client.file_reloading.provided_QMARK_.call(null,cljs.core.name.call(null,namespace));
if(cljs.core.truth_(or__4131__auto____$2)){
return or__4131__auto____$2;
} else {
return figwheel.client.file_reloading.ns_exists_QMARK_.call(null,namespace);
}
}
}
} else {
return and__4120__auto__;
}
});
figwheel.client.file_reloading.js_reload = (function figwheel$client$file_reloading$js_reload(p__35884,callback){
var map__35885 = p__35884;
var map__35885__$1 = (((((!((map__35885 == null))))?(((((map__35885.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35885.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35885):map__35885);
var file_msg = map__35885__$1;
var request_url = cljs.core.get.call(null,map__35885__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));
var namespace = cljs.core.get.call(null,map__35885__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));

if(cljs.core.truth_(figwheel.client.file_reloading.reload_file_QMARK_.call(null,file_msg))){
return figwheel.client.file_reloading.require_with_callback.call(null,file_msg,callback);
} else {
figwheel.client.utils.debug_prn.call(null,["Figwheel: Not trying to load file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [file_msg], null));
}
});
figwheel.client.file_reloading.reload_js_file = (function figwheel$client$file_reloading$reload_js_file(file_msg){
var out = cljs.core.async.chan.call(null);
figwheel.client.file_reloading.js_reload.call(null,file_msg,((function (out){
return (function (url){
cljs.core.async.put_BANG_.call(null,out,url);

return cljs.core.async.close_BANG_.call(null,out);
});})(out))
);

return out;
});
/**
 * Returns a chanel with one collection of loaded filenames on it.
 */
figwheel.client.file_reloading.load_all_js_files = (function figwheel$client$file_reloading$load_all_js_files(files){
var out = cljs.core.async.chan.call(null);
var c__33495__auto___35935 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___35935,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___35935,out){
return (function (state_35920){
var state_val_35921 = (state_35920[(1)]);
if((state_val_35921 === (1))){
var inst_35894 = cljs.core.seq.call(null,files);
var inst_35895 = cljs.core.first.call(null,inst_35894);
var inst_35896 = cljs.core.next.call(null,inst_35894);
var inst_35897 = files;
var state_35920__$1 = (function (){var statearr_35922 = state_35920;
(statearr_35922[(7)] = inst_35895);

(statearr_35922[(8)] = inst_35897);

(statearr_35922[(9)] = inst_35896);

return statearr_35922;
})();
var statearr_35923_35936 = state_35920__$1;
(statearr_35923_35936[(2)] = null);

(statearr_35923_35936[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35921 === (2))){
var inst_35903 = (state_35920[(10)]);
var inst_35897 = (state_35920[(8)]);
var inst_35902 = cljs.core.seq.call(null,inst_35897);
var inst_35903__$1 = cljs.core.first.call(null,inst_35902);
var inst_35904 = cljs.core.next.call(null,inst_35902);
var inst_35905 = (inst_35903__$1 == null);
var inst_35906 = cljs.core.not.call(null,inst_35905);
var state_35920__$1 = (function (){var statearr_35924 = state_35920;
(statearr_35924[(10)] = inst_35903__$1);

(statearr_35924[(11)] = inst_35904);

return statearr_35924;
})();
if(inst_35906){
var statearr_35925_35937 = state_35920__$1;
(statearr_35925_35937[(1)] = (4));

} else {
var statearr_35926_35938 = state_35920__$1;
(statearr_35926_35938[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35921 === (3))){
var inst_35918 = (state_35920[(2)]);
var state_35920__$1 = state_35920;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35920__$1,inst_35918);
} else {
if((state_val_35921 === (4))){
var inst_35903 = (state_35920[(10)]);
var inst_35908 = figwheel.client.file_reloading.reload_js_file.call(null,inst_35903);
var state_35920__$1 = state_35920;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35920__$1,(7),inst_35908);
} else {
if((state_val_35921 === (5))){
var inst_35914 = cljs.core.async.close_BANG_.call(null,out);
var state_35920__$1 = state_35920;
var statearr_35927_35939 = state_35920__$1;
(statearr_35927_35939[(2)] = inst_35914);

(statearr_35927_35939[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35921 === (6))){
var inst_35916 = (state_35920[(2)]);
var state_35920__$1 = state_35920;
var statearr_35928_35940 = state_35920__$1;
(statearr_35928_35940[(2)] = inst_35916);

(statearr_35928_35940[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35921 === (7))){
var inst_35904 = (state_35920[(11)]);
var inst_35910 = (state_35920[(2)]);
var inst_35911 = cljs.core.async.put_BANG_.call(null,out,inst_35910);
var inst_35897 = inst_35904;
var state_35920__$1 = (function (){var statearr_35929 = state_35920;
(statearr_35929[(8)] = inst_35897);

(statearr_35929[(12)] = inst_35911);

return statearr_35929;
})();
var statearr_35930_35941 = state_35920__$1;
(statearr_35930_35941[(2)] = null);

(statearr_35930_35941[(1)] = (2));


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
});})(c__33495__auto___35935,out))
;
return ((function (switch__33350__auto__,c__33495__auto___35935,out){
return (function() {
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__ = null;
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____0 = (function (){
var statearr_35931 = [null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_35931[(0)] = figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__);

(statearr_35931[(1)] = (1));

return statearr_35931;
});
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____1 = (function (state_35920){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35920);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35932){if((e35932 instanceof Object)){
var ex__33354__auto__ = e35932;
var statearr_35933_35942 = state_35920;
(statearr_35933_35942[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35920);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35932;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35943 = state_35920;
state_35920 = G__35943;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__ = function(state_35920){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____1.call(this,state_35920);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____0;
figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto____1;
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___35935,out))
})();
var state__33497__auto__ = (function (){var statearr_35934 = f__33496__auto__.call(null);
(statearr_35934[(6)] = c__33495__auto___35935);

return statearr_35934;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___35935,out))
);


return cljs.core.async.into.call(null,cljs.core.PersistentVector.EMPTY,out);
});
figwheel.client.file_reloading.eval_body = (function figwheel$client$file_reloading$eval_body(p__35944,opts){
var map__35945 = p__35944;
var map__35945__$1 = (((((!((map__35945 == null))))?(((((map__35945.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35945.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35945):map__35945);
var eval_body = cljs.core.get.call(null,map__35945__$1,new cljs.core.Keyword(null,"eval-body","eval-body",-907279883));
var file = cljs.core.get.call(null,map__35945__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
if(cljs.core.truth_((function (){var and__4120__auto__ = eval_body;
if(cljs.core.truth_(and__4120__auto__)){
return typeof eval_body === 'string';
} else {
return and__4120__auto__;
}
})())){
var code = eval_body;
try{figwheel.client.utils.debug_prn.call(null,["Evaling file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(file)].join(''));

return figwheel.client.utils.eval_helper.call(null,code,opts);
}catch (e35947){var e = e35947;
return figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Unable to evaluate ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(file)].join(''));
}} else {
return null;
}
});
figwheel.client.file_reloading.expand_files = (function figwheel$client$file_reloading$expand_files(files){
var deps = figwheel.client.file_reloading.get_all_dependents.call(null,cljs.core.map.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372),files));
return cljs.core.filter.call(null,cljs.core.comp.call(null,cljs.core.not,cljs.core.partial.call(null,cljs.core.re_matches,/figwheel\.connect.*/),new cljs.core.Keyword(null,"namespace","namespace",-377510372)),cljs.core.map.call(null,((function (deps){
return (function (n){
var temp__5751__auto__ = cljs.core.first.call(null,cljs.core.filter.call(null,((function (deps){
return (function (p1__35948_SHARP_){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(p1__35948_SHARP_),n);
});})(deps))
,files));
if(cljs.core.truth_(temp__5751__auto__)){
var file_msg = temp__5751__auto__;
return file_msg;
} else {
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"namespace","namespace",-377510372),new cljs.core.Keyword(null,"namespace","namespace",-377510372),n], null);
}
});})(deps))
,deps));
});
figwheel.client.file_reloading.sort_files = (function figwheel$client$file_reloading$sort_files(files){
if((cljs.core.count.call(null,files) <= (1))){
return files;
} else {
var keep_files = cljs.core.set.call(null,cljs.core.keep.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372),files));
return cljs.core.filter.call(null,cljs.core.comp.call(null,keep_files,new cljs.core.Keyword(null,"namespace","namespace",-377510372)),figwheel.client.file_reloading.expand_files.call(null,files));
}
});
figwheel.client.file_reloading.get_figwheel_always = (function figwheel$client$file_reloading$get_figwheel_always(){
return cljs.core.map.call(null,(function (p__35949){
var vec__35950 = p__35949;
var k = cljs.core.nth.call(null,vec__35950,(0),null);
var v = cljs.core.nth.call(null,vec__35950,(1),null);
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"namespace","namespace",-377510372),k,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"namespace","namespace",-377510372)], null);
}),cljs.core.filter.call(null,(function (p__35953){
var vec__35954 = p__35953;
var k = cljs.core.nth.call(null,vec__35954,(0),null);
var v = cljs.core.nth.call(null,vec__35954,(1),null);
return new cljs.core.Keyword(null,"figwheel-always","figwheel-always",799819691).cljs$core$IFn$_invoke$arity$1(v);
}),cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas)));
});
figwheel.client.file_reloading.reload_js_files = (function figwheel$client$file_reloading$reload_js_files(p__35960,p__35961){
var map__35962 = p__35960;
var map__35962__$1 = (((((!((map__35962 == null))))?(((((map__35962.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35962.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35962):map__35962);
var opts = map__35962__$1;
var before_jsload = cljs.core.get.call(null,map__35962__$1,new cljs.core.Keyword(null,"before-jsload","before-jsload",-847513128));
var on_jsload = cljs.core.get.call(null,map__35962__$1,new cljs.core.Keyword(null,"on-jsload","on-jsload",-395756602));
var reload_dependents = cljs.core.get.call(null,map__35962__$1,new cljs.core.Keyword(null,"reload-dependents","reload-dependents",-956865430));
var map__35963 = p__35961;
var map__35963__$1 = (((((!((map__35963 == null))))?(((((map__35963.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35963.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35963):map__35963);
var msg = map__35963__$1;
var files = cljs.core.get.call(null,map__35963__$1,new cljs.core.Keyword(null,"files","files",-472457450));
var figwheel_meta = cljs.core.get.call(null,map__35963__$1,new cljs.core.Keyword(null,"figwheel-meta","figwheel-meta",-225970237));
var recompile_dependents = cljs.core.get.call(null,map__35963__$1,new cljs.core.Keyword(null,"recompile-dependents","recompile-dependents",523804171));
if(cljs.core.empty_QMARK_.call(null,figwheel_meta)){
} else {
cljs.core.reset_BANG_.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas,figwheel_meta);
}

var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (state_36117){
var state_val_36118 = (state_36117[(1)]);
if((state_val_36118 === (7))){
var inst_35978 = (state_36117[(7)]);
var inst_35979 = (state_36117[(8)]);
var inst_35980 = (state_36117[(9)]);
var inst_35977 = (state_36117[(10)]);
var inst_35985 = cljs.core._nth.call(null,inst_35978,inst_35980);
var inst_35986 = figwheel.client.file_reloading.eval_body.call(null,inst_35985,opts);
var inst_35987 = (inst_35980 + (1));
var tmp36119 = inst_35978;
var tmp36120 = inst_35979;
var tmp36121 = inst_35977;
var inst_35977__$1 = tmp36121;
var inst_35978__$1 = tmp36119;
var inst_35979__$1 = tmp36120;
var inst_35980__$1 = inst_35987;
var state_36117__$1 = (function (){var statearr_36122 = state_36117;
(statearr_36122[(7)] = inst_35978__$1);

(statearr_36122[(8)] = inst_35979__$1);

(statearr_36122[(9)] = inst_35980__$1);

(statearr_36122[(11)] = inst_35986);

(statearr_36122[(10)] = inst_35977__$1);

return statearr_36122;
})();
var statearr_36123_36206 = state_36117__$1;
(statearr_36123_36206[(2)] = null);

(statearr_36123_36206[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (20))){
var inst_36020 = (state_36117[(12)]);
var inst_36028 = figwheel.client.file_reloading.sort_files.call(null,inst_36020);
var state_36117__$1 = state_36117;
var statearr_36124_36207 = state_36117__$1;
(statearr_36124_36207[(2)] = inst_36028);

(statearr_36124_36207[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (27))){
var state_36117__$1 = state_36117;
var statearr_36125_36208 = state_36117__$1;
(statearr_36125_36208[(2)] = null);

(statearr_36125_36208[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (1))){
var inst_35969 = (state_36117[(13)]);
var inst_35966 = before_jsload.call(null,files);
var inst_35967 = figwheel.client.file_reloading.before_jsload_custom_event.call(null,files);
var inst_35968 = (function (){return ((function (inst_35969,inst_35966,inst_35967,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__35957_SHARP_){
return new cljs.core.Keyword(null,"eval-body","eval-body",-907279883).cljs$core$IFn$_invoke$arity$1(p1__35957_SHARP_);
});
;})(inst_35969,inst_35966,inst_35967,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_35969__$1 = cljs.core.filter.call(null,inst_35968,files);
var inst_35970 = cljs.core.not_empty.call(null,inst_35969__$1);
var state_36117__$1 = (function (){var statearr_36126 = state_36117;
(statearr_36126[(13)] = inst_35969__$1);

(statearr_36126[(14)] = inst_35967);

(statearr_36126[(15)] = inst_35966);

return statearr_36126;
})();
if(cljs.core.truth_(inst_35970)){
var statearr_36127_36209 = state_36117__$1;
(statearr_36127_36209[(1)] = (2));

} else {
var statearr_36128_36210 = state_36117__$1;
(statearr_36128_36210[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (24))){
var state_36117__$1 = state_36117;
var statearr_36129_36211 = state_36117__$1;
(statearr_36129_36211[(2)] = null);

(statearr_36129_36211[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (39))){
var inst_36070 = (state_36117[(16)]);
var state_36117__$1 = state_36117;
var statearr_36130_36212 = state_36117__$1;
(statearr_36130_36212[(2)] = inst_36070);

(statearr_36130_36212[(1)] = (40));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (46))){
var inst_36112 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36131_36213 = state_36117__$1;
(statearr_36131_36213[(2)] = inst_36112);

(statearr_36131_36213[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (4))){
var inst_36014 = (state_36117[(2)]);
var inst_36015 = cljs.core.List.EMPTY;
var inst_36016 = cljs.core.reset_BANG_.call(null,figwheel.client.file_reloading.dependencies_loaded,inst_36015);
var inst_36017 = (function (){return ((function (inst_36014,inst_36015,inst_36016,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__35958_SHARP_){
var and__4120__auto__ = new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(p1__35958_SHARP_);
if(cljs.core.truth_(and__4120__auto__)){
return ((cljs.core.not.call(null,new cljs.core.Keyword(null,"eval-body","eval-body",-907279883).cljs$core$IFn$_invoke$arity$1(p1__35958_SHARP_))) && (cljs.core.not.call(null,figwheel.client.file_reloading.figwheel_no_load_QMARK_.call(null,p1__35958_SHARP_))));
} else {
return and__4120__auto__;
}
});
;})(inst_36014,inst_36015,inst_36016,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36018 = cljs.core.filter.call(null,inst_36017,files);
var inst_36019 = figwheel.client.file_reloading.get_figwheel_always.call(null);
var inst_36020 = cljs.core.concat.call(null,inst_36018,inst_36019);
var state_36117__$1 = (function (){var statearr_36132 = state_36117;
(statearr_36132[(12)] = inst_36020);

(statearr_36132[(17)] = inst_36016);

(statearr_36132[(18)] = inst_36014);

return statearr_36132;
})();
if(cljs.core.truth_(reload_dependents)){
var statearr_36133_36214 = state_36117__$1;
(statearr_36133_36214[(1)] = (16));

} else {
var statearr_36134_36215 = state_36117__$1;
(statearr_36134_36215[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (15))){
var inst_36004 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36135_36216 = state_36117__$1;
(statearr_36135_36216[(2)] = inst_36004);

(statearr_36135_36216[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (21))){
var inst_36030 = (state_36117[(19)]);
var inst_36030__$1 = (state_36117[(2)]);
var inst_36031 = figwheel.client.file_reloading.load_all_js_files.call(null,inst_36030__$1);
var state_36117__$1 = (function (){var statearr_36136 = state_36117;
(statearr_36136[(19)] = inst_36030__$1);

return statearr_36136;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_36117__$1,(22),inst_36031);
} else {
if((state_val_36118 === (31))){
var inst_36115 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_36117__$1,inst_36115);
} else {
if((state_val_36118 === (32))){
var inst_36070 = (state_36117[(16)]);
var inst_36075 = inst_36070.cljs$lang$protocol_mask$partition0$;
var inst_36076 = (inst_36075 & (64));
var inst_36077 = inst_36070.cljs$core$ISeq$;
var inst_36078 = (cljs.core.PROTOCOL_SENTINEL === inst_36077);
var inst_36079 = ((inst_36076) || (inst_36078));
var state_36117__$1 = state_36117;
if(cljs.core.truth_(inst_36079)){
var statearr_36137_36217 = state_36117__$1;
(statearr_36137_36217[(1)] = (35));

} else {
var statearr_36138_36218 = state_36117__$1;
(statearr_36138_36218[(1)] = (36));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (40))){
var inst_36092 = (state_36117[(20)]);
var inst_36091 = (state_36117[(2)]);
var inst_36092__$1 = cljs.core.get.call(null,inst_36091,new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179));
var inst_36093 = cljs.core.get.call(null,inst_36091,new cljs.core.Keyword(null,"not-required","not-required",-950359114));
var inst_36094 = cljs.core.not_empty.call(null,inst_36092__$1);
var state_36117__$1 = (function (){var statearr_36139 = state_36117;
(statearr_36139[(20)] = inst_36092__$1);

(statearr_36139[(21)] = inst_36093);

return statearr_36139;
})();
if(cljs.core.truth_(inst_36094)){
var statearr_36140_36219 = state_36117__$1;
(statearr_36140_36219[(1)] = (41));

} else {
var statearr_36141_36220 = state_36117__$1;
(statearr_36141_36220[(1)] = (42));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (33))){
var state_36117__$1 = state_36117;
var statearr_36142_36221 = state_36117__$1;
(statearr_36142_36221[(2)] = false);

(statearr_36142_36221[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (13))){
var inst_35990 = (state_36117[(22)]);
var inst_35994 = cljs.core.chunk_first.call(null,inst_35990);
var inst_35995 = cljs.core.chunk_rest.call(null,inst_35990);
var inst_35996 = cljs.core.count.call(null,inst_35994);
var inst_35977 = inst_35995;
var inst_35978 = inst_35994;
var inst_35979 = inst_35996;
var inst_35980 = (0);
var state_36117__$1 = (function (){var statearr_36143 = state_36117;
(statearr_36143[(7)] = inst_35978);

(statearr_36143[(8)] = inst_35979);

(statearr_36143[(9)] = inst_35980);

(statearr_36143[(10)] = inst_35977);

return statearr_36143;
})();
var statearr_36144_36222 = state_36117__$1;
(statearr_36144_36222[(2)] = null);

(statearr_36144_36222[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (22))){
var inst_36034 = (state_36117[(23)]);
var inst_36038 = (state_36117[(24)]);
var inst_36033 = (state_36117[(25)]);
var inst_36030 = (state_36117[(19)]);
var inst_36033__$1 = (state_36117[(2)]);
var inst_36034__$1 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),inst_36033__$1);
var inst_36035 = (function (){var all_files = inst_36030;
var res_SINGLEQUOTE_ = inst_36033__$1;
var res = inst_36034__$1;
return ((function (all_files,res_SINGLEQUOTE_,res,inst_36034,inst_36038,inst_36033,inst_36030,inst_36033__$1,inst_36034__$1,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__35959_SHARP_){
return cljs.core.not.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375).cljs$core$IFn$_invoke$arity$1(p1__35959_SHARP_));
});
;})(all_files,res_SINGLEQUOTE_,res,inst_36034,inst_36038,inst_36033,inst_36030,inst_36033__$1,inst_36034__$1,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36036 = cljs.core.filter.call(null,inst_36035,inst_36033__$1);
var inst_36037 = cljs.core.deref.call(null,figwheel.client.file_reloading.dependencies_loaded);
var inst_36038__$1 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),inst_36037);
var inst_36039 = cljs.core.not_empty.call(null,inst_36038__$1);
var state_36117__$1 = (function (){var statearr_36145 = state_36117;
(statearr_36145[(26)] = inst_36036);

(statearr_36145[(23)] = inst_36034__$1);

(statearr_36145[(24)] = inst_36038__$1);

(statearr_36145[(25)] = inst_36033__$1);

return statearr_36145;
})();
if(cljs.core.truth_(inst_36039)){
var statearr_36146_36223 = state_36117__$1;
(statearr_36146_36223[(1)] = (23));

} else {
var statearr_36147_36224 = state_36117__$1;
(statearr_36147_36224[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (36))){
var state_36117__$1 = state_36117;
var statearr_36148_36225 = state_36117__$1;
(statearr_36148_36225[(2)] = false);

(statearr_36148_36225[(1)] = (37));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (41))){
var inst_36092 = (state_36117[(20)]);
var inst_36096 = cljs.core.comp.call(null,figwheel.client.file_reloading.name__GT_path,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var inst_36097 = cljs.core.map.call(null,inst_36096,inst_36092);
var inst_36098 = cljs.core.pr_str.call(null,inst_36097);
var inst_36099 = ["figwheel-no-load meta-data: ",inst_36098].join('');
var inst_36100 = figwheel.client.utils.log.call(null,inst_36099);
var state_36117__$1 = state_36117;
var statearr_36149_36226 = state_36117__$1;
(statearr_36149_36226[(2)] = inst_36100);

(statearr_36149_36226[(1)] = (43));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (43))){
var inst_36093 = (state_36117[(21)]);
var inst_36103 = (state_36117[(2)]);
var inst_36104 = cljs.core.not_empty.call(null,inst_36093);
var state_36117__$1 = (function (){var statearr_36150 = state_36117;
(statearr_36150[(27)] = inst_36103);

return statearr_36150;
})();
if(cljs.core.truth_(inst_36104)){
var statearr_36151_36227 = state_36117__$1;
(statearr_36151_36227[(1)] = (44));

} else {
var statearr_36152_36228 = state_36117__$1;
(statearr_36152_36228[(1)] = (45));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (29))){
var inst_36036 = (state_36117[(26)]);
var inst_36070 = (state_36117[(16)]);
var inst_36034 = (state_36117[(23)]);
var inst_36038 = (state_36117[(24)]);
var inst_36033 = (state_36117[(25)]);
var inst_36030 = (state_36117[(19)]);
var inst_36066 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: NOT loading these files ");
var inst_36069 = (function (){var all_files = inst_36030;
var res_SINGLEQUOTE_ = inst_36033;
var res = inst_36034;
var files_not_loaded = inst_36036;
var dependencies_that_loaded = inst_36038;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36070,inst_36034,inst_36038,inst_36033,inst_36030,inst_36066,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__36068){
var map__36153 = p__36068;
var map__36153__$1 = (((((!((map__36153 == null))))?(((((map__36153.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36153.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36153):map__36153);
var namespace = cljs.core.get.call(null,map__36153__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var meta_data = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
if((meta_data == null)){
return new cljs.core.Keyword(null,"not-required","not-required",-950359114);
} else {
if(cljs.core.truth_(meta_data.call(null,new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179)))){
return new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179);
} else {
return new cljs.core.Keyword(null,"not-required","not-required",-950359114);

}
}
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36070,inst_36034,inst_36038,inst_36033,inst_36030,inst_36066,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36070__$1 = cljs.core.group_by.call(null,inst_36069,inst_36036);
var inst_36072 = (inst_36070__$1 == null);
var inst_36073 = cljs.core.not.call(null,inst_36072);
var state_36117__$1 = (function (){var statearr_36155 = state_36117;
(statearr_36155[(16)] = inst_36070__$1);

(statearr_36155[(28)] = inst_36066);

return statearr_36155;
})();
if(inst_36073){
var statearr_36156_36229 = state_36117__$1;
(statearr_36156_36229[(1)] = (32));

} else {
var statearr_36157_36230 = state_36117__$1;
(statearr_36157_36230[(1)] = (33));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (44))){
var inst_36093 = (state_36117[(21)]);
var inst_36106 = cljs.core.map.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),inst_36093);
var inst_36107 = cljs.core.pr_str.call(null,inst_36106);
var inst_36108 = ["not required: ",inst_36107].join('');
var inst_36109 = figwheel.client.utils.log.call(null,inst_36108);
var state_36117__$1 = state_36117;
var statearr_36158_36231 = state_36117__$1;
(statearr_36158_36231[(2)] = inst_36109);

(statearr_36158_36231[(1)] = (46));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (6))){
var inst_36011 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36159_36232 = state_36117__$1;
(statearr_36159_36232[(2)] = inst_36011);

(statearr_36159_36232[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (28))){
var inst_36036 = (state_36117[(26)]);
var inst_36063 = (state_36117[(2)]);
var inst_36064 = cljs.core.not_empty.call(null,inst_36036);
var state_36117__$1 = (function (){var statearr_36160 = state_36117;
(statearr_36160[(29)] = inst_36063);

return statearr_36160;
})();
if(cljs.core.truth_(inst_36064)){
var statearr_36161_36233 = state_36117__$1;
(statearr_36161_36233[(1)] = (29));

} else {
var statearr_36162_36234 = state_36117__$1;
(statearr_36162_36234[(1)] = (30));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (25))){
var inst_36034 = (state_36117[(23)]);
var inst_36050 = (state_36117[(2)]);
var inst_36051 = cljs.core.not_empty.call(null,inst_36034);
var state_36117__$1 = (function (){var statearr_36163 = state_36117;
(statearr_36163[(30)] = inst_36050);

return statearr_36163;
})();
if(cljs.core.truth_(inst_36051)){
var statearr_36164_36235 = state_36117__$1;
(statearr_36164_36235[(1)] = (26));

} else {
var statearr_36165_36236 = state_36117__$1;
(statearr_36165_36236[(1)] = (27));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (34))){
var inst_36086 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
if(cljs.core.truth_(inst_36086)){
var statearr_36166_36237 = state_36117__$1;
(statearr_36166_36237[(1)] = (38));

} else {
var statearr_36167_36238 = state_36117__$1;
(statearr_36167_36238[(1)] = (39));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (17))){
var state_36117__$1 = state_36117;
var statearr_36168_36239 = state_36117__$1;
(statearr_36168_36239[(2)] = recompile_dependents);

(statearr_36168_36239[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (3))){
var state_36117__$1 = state_36117;
var statearr_36169_36240 = state_36117__$1;
(statearr_36169_36240[(2)] = null);

(statearr_36169_36240[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (12))){
var inst_36007 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36170_36241 = state_36117__$1;
(statearr_36170_36241[(2)] = inst_36007);

(statearr_36170_36241[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (2))){
var inst_35969 = (state_36117[(13)]);
var inst_35976 = cljs.core.seq.call(null,inst_35969);
var inst_35977 = inst_35976;
var inst_35978 = null;
var inst_35979 = (0);
var inst_35980 = (0);
var state_36117__$1 = (function (){var statearr_36171 = state_36117;
(statearr_36171[(7)] = inst_35978);

(statearr_36171[(8)] = inst_35979);

(statearr_36171[(9)] = inst_35980);

(statearr_36171[(10)] = inst_35977);

return statearr_36171;
})();
var statearr_36172_36242 = state_36117__$1;
(statearr_36172_36242[(2)] = null);

(statearr_36172_36242[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (23))){
var inst_36036 = (state_36117[(26)]);
var inst_36034 = (state_36117[(23)]);
var inst_36038 = (state_36117[(24)]);
var inst_36033 = (state_36117[(25)]);
var inst_36030 = (state_36117[(19)]);
var inst_36041 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: loaded these dependencies");
var inst_36043 = (function (){var all_files = inst_36030;
var res_SINGLEQUOTE_ = inst_36033;
var res = inst_36034;
var files_not_loaded = inst_36036;
var dependencies_that_loaded = inst_36038;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36041,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__36042){
var map__36173 = p__36042;
var map__36173__$1 = (((((!((map__36173 == null))))?(((((map__36173.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36173.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36173):map__36173);
var request_url = cljs.core.get.call(null,map__36173__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));
return clojure.string.replace.call(null,request_url,goog.basePath,"");
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36041,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36044 = cljs.core.reverse.call(null,inst_36038);
var inst_36045 = cljs.core.map.call(null,inst_36043,inst_36044);
var inst_36046 = cljs.core.pr_str.call(null,inst_36045);
var inst_36047 = figwheel.client.utils.log.call(null,inst_36046);
var state_36117__$1 = (function (){var statearr_36175 = state_36117;
(statearr_36175[(31)] = inst_36041);

return statearr_36175;
})();
var statearr_36176_36243 = state_36117__$1;
(statearr_36176_36243[(2)] = inst_36047);

(statearr_36176_36243[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (35))){
var state_36117__$1 = state_36117;
var statearr_36177_36244 = state_36117__$1;
(statearr_36177_36244[(2)] = true);

(statearr_36177_36244[(1)] = (37));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (19))){
var inst_36020 = (state_36117[(12)]);
var inst_36026 = figwheel.client.file_reloading.expand_files.call(null,inst_36020);
var state_36117__$1 = state_36117;
var statearr_36178_36245 = state_36117__$1;
(statearr_36178_36245[(2)] = inst_36026);

(statearr_36178_36245[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (11))){
var state_36117__$1 = state_36117;
var statearr_36179_36246 = state_36117__$1;
(statearr_36179_36246[(2)] = null);

(statearr_36179_36246[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (9))){
var inst_36009 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36180_36247 = state_36117__$1;
(statearr_36180_36247[(2)] = inst_36009);

(statearr_36180_36247[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (5))){
var inst_35979 = (state_36117[(8)]);
var inst_35980 = (state_36117[(9)]);
var inst_35982 = (inst_35980 < inst_35979);
var inst_35983 = inst_35982;
var state_36117__$1 = state_36117;
if(cljs.core.truth_(inst_35983)){
var statearr_36181_36248 = state_36117__$1;
(statearr_36181_36248[(1)] = (7));

} else {
var statearr_36182_36249 = state_36117__$1;
(statearr_36182_36249[(1)] = (8));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (14))){
var inst_35990 = (state_36117[(22)]);
var inst_35999 = cljs.core.first.call(null,inst_35990);
var inst_36000 = figwheel.client.file_reloading.eval_body.call(null,inst_35999,opts);
var inst_36001 = cljs.core.next.call(null,inst_35990);
var inst_35977 = inst_36001;
var inst_35978 = null;
var inst_35979 = (0);
var inst_35980 = (0);
var state_36117__$1 = (function (){var statearr_36183 = state_36117;
(statearr_36183[(7)] = inst_35978);

(statearr_36183[(8)] = inst_35979);

(statearr_36183[(32)] = inst_36000);

(statearr_36183[(9)] = inst_35980);

(statearr_36183[(10)] = inst_35977);

return statearr_36183;
})();
var statearr_36184_36250 = state_36117__$1;
(statearr_36184_36250[(2)] = null);

(statearr_36184_36250[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (45))){
var state_36117__$1 = state_36117;
var statearr_36185_36251 = state_36117__$1;
(statearr_36185_36251[(2)] = null);

(statearr_36185_36251[(1)] = (46));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (26))){
var inst_36036 = (state_36117[(26)]);
var inst_36034 = (state_36117[(23)]);
var inst_36038 = (state_36117[(24)]);
var inst_36033 = (state_36117[(25)]);
var inst_36030 = (state_36117[(19)]);
var inst_36053 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: loaded these files");
var inst_36055 = (function (){var all_files = inst_36030;
var res_SINGLEQUOTE_ = inst_36033;
var res = inst_36034;
var files_not_loaded = inst_36036;
var dependencies_that_loaded = inst_36038;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36053,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__36054){
var map__36186 = p__36054;
var map__36186__$1 = (((((!((map__36186 == null))))?(((((map__36186.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36186.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36186):map__36186);
var namespace = cljs.core.get.call(null,map__36186__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var file = cljs.core.get.call(null,map__36186__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
if(cljs.core.truth_(namespace)){
return figwheel.client.file_reloading.name__GT_path.call(null,cljs.core.name.call(null,namespace));
} else {
return file;
}
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36053,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36056 = cljs.core.map.call(null,inst_36055,inst_36034);
var inst_36057 = cljs.core.pr_str.call(null,inst_36056);
var inst_36058 = figwheel.client.utils.log.call(null,inst_36057);
var inst_36059 = (function (){var all_files = inst_36030;
var res_SINGLEQUOTE_ = inst_36033;
var res = inst_36034;
var files_not_loaded = inst_36036;
var dependencies_that_loaded = inst_36038;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36053,inst_36055,inst_36056,inst_36057,inst_36058,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (){
figwheel.client.file_reloading.on_jsload_custom_event.call(null,res);

return cljs.core.apply.call(null,on_jsload,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [res], null));
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_36036,inst_36034,inst_36038,inst_36033,inst_36030,inst_36053,inst_36055,inst_36056,inst_36057,inst_36058,state_val_36118,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_36060 = setTimeout(inst_36059,(10));
var state_36117__$1 = (function (){var statearr_36188 = state_36117;
(statearr_36188[(33)] = inst_36058);

(statearr_36188[(34)] = inst_36053);

return statearr_36188;
})();
var statearr_36189_36252 = state_36117__$1;
(statearr_36189_36252[(2)] = inst_36060);

(statearr_36189_36252[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (16))){
var state_36117__$1 = state_36117;
var statearr_36190_36253 = state_36117__$1;
(statearr_36190_36253[(2)] = reload_dependents);

(statearr_36190_36253[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (38))){
var inst_36070 = (state_36117[(16)]);
var inst_36088 = cljs.core.apply.call(null,cljs.core.hash_map,inst_36070);
var state_36117__$1 = state_36117;
var statearr_36191_36254 = state_36117__$1;
(statearr_36191_36254[(2)] = inst_36088);

(statearr_36191_36254[(1)] = (40));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (30))){
var state_36117__$1 = state_36117;
var statearr_36192_36255 = state_36117__$1;
(statearr_36192_36255[(2)] = null);

(statearr_36192_36255[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (10))){
var inst_35990 = (state_36117[(22)]);
var inst_35992 = cljs.core.chunked_seq_QMARK_.call(null,inst_35990);
var state_36117__$1 = state_36117;
if(inst_35992){
var statearr_36193_36256 = state_36117__$1;
(statearr_36193_36256[(1)] = (13));

} else {
var statearr_36194_36257 = state_36117__$1;
(statearr_36194_36257[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (18))){
var inst_36024 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
if(cljs.core.truth_(inst_36024)){
var statearr_36195_36258 = state_36117__$1;
(statearr_36195_36258[(1)] = (19));

} else {
var statearr_36196_36259 = state_36117__$1;
(statearr_36196_36259[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (42))){
var state_36117__$1 = state_36117;
var statearr_36197_36260 = state_36117__$1;
(statearr_36197_36260[(2)] = null);

(statearr_36197_36260[(1)] = (43));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (37))){
var inst_36083 = (state_36117[(2)]);
var state_36117__$1 = state_36117;
var statearr_36198_36261 = state_36117__$1;
(statearr_36198_36261[(2)] = inst_36083);

(statearr_36198_36261[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_36118 === (8))){
var inst_35990 = (state_36117[(22)]);
var inst_35977 = (state_36117[(10)]);
var inst_35990__$1 = cljs.core.seq.call(null,inst_35977);
var state_36117__$1 = (function (){var statearr_36199 = state_36117;
(statearr_36199[(22)] = inst_35990__$1);

return statearr_36199;
})();
if(inst_35990__$1){
var statearr_36200_36262 = state_36117__$1;
(statearr_36200_36262[(1)] = (10));

} else {
var statearr_36201_36263 = state_36117__$1;
(statearr_36201_36263[(1)] = (11));

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
});})(c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
;
return ((function (switch__33350__auto__,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents){
return (function() {
var figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__ = null;
var figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____0 = (function (){
var statearr_36202 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_36202[(0)] = figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__);

(statearr_36202[(1)] = (1));

return statearr_36202;
});
var figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____1 = (function (state_36117){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_36117);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e36203){if((e36203 instanceof Object)){
var ex__33354__auto__ = e36203;
var statearr_36204_36264 = state_36117;
(statearr_36204_36264[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_36117);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e36203;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__36265 = state_36117;
state_36117 = G__36265;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__ = function(state_36117){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____1.call(this,state_36117);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____0;
figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto____1;
return figwheel$client$file_reloading$reload_js_files_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var state__33497__auto__ = (function (){var statearr_36205 = f__33496__auto__.call(null);
(statearr_36205[(6)] = c__33495__auto__);

return statearr_36205;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__,map__35962,map__35962__$1,opts,before_jsload,on_jsload,reload_dependents,map__35963,map__35963__$1,msg,files,figwheel_meta,recompile_dependents))
);

return c__33495__auto__;
});
figwheel.client.file_reloading.current_links = (function figwheel$client$file_reloading$current_links(){
return Array.prototype.slice.call(document.getElementsByTagName("link"));
});
figwheel.client.file_reloading.truncate_url = (function figwheel$client$file_reloading$truncate_url(url){
return clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,cljs.core.first.call(null,clojure.string.split.call(null,url,/\?/)),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(location.protocol),"//"].join(''),""),".*://",""),/^\/\//,""),/[^\\/]*/,"");
});
figwheel.client.file_reloading.matches_file_QMARK_ = (function figwheel$client$file_reloading$matches_file_QMARK_(p__36268,link){
var map__36269 = p__36268;
var map__36269__$1 = (((((!((map__36269 == null))))?(((((map__36269.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36269.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36269):map__36269);
var file = cljs.core.get.call(null,map__36269__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
var temp__5753__auto__ = link.href;
if(cljs.core.truth_(temp__5753__auto__)){
var link_href = temp__5753__auto__;
var match = clojure.string.join.call(null,"/",cljs.core.take_while.call(null,cljs.core.identity,cljs.core.map.call(null,((function (link_href,temp__5753__auto__,map__36269,map__36269__$1,file){
return (function (p1__36266_SHARP_,p2__36267_SHARP_){
if(cljs.core._EQ_.call(null,p1__36266_SHARP_,p2__36267_SHARP_)){
return p1__36266_SHARP_;
} else {
return false;
}
});})(link_href,temp__5753__auto__,map__36269,map__36269__$1,file))
,cljs.core.reverse.call(null,clojure.string.split.call(null,file,"/")),cljs.core.reverse.call(null,clojure.string.split.call(null,figwheel.client.file_reloading.truncate_url.call(null,link_href),"/")))));
var match_length = cljs.core.count.call(null,match);
var file_name_length = cljs.core.count.call(null,cljs.core.last.call(null,clojure.string.split.call(null,file,"/")));
if((match_length >= file_name_length)){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"link","link",-1769163468),link,new cljs.core.Keyword(null,"link-href","link-href",-250644450),link_href,new cljs.core.Keyword(null,"match-length","match-length",1101537310),match_length,new cljs.core.Keyword(null,"current-url-length","current-url-length",380404083),cljs.core.count.call(null,figwheel.client.file_reloading.truncate_url.call(null,link_href))], null);
} else {
return null;
}
} else {
return null;
}
});
figwheel.client.file_reloading.get_correct_link = (function figwheel$client$file_reloading$get_correct_link(f_data){
var temp__5753__auto__ = cljs.core.first.call(null,cljs.core.sort_by.call(null,(function (p__36272){
var map__36273 = p__36272;
var map__36273__$1 = (((((!((map__36273 == null))))?(((((map__36273.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36273.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36273):map__36273);
var match_length = cljs.core.get.call(null,map__36273__$1,new cljs.core.Keyword(null,"match-length","match-length",1101537310));
var current_url_length = cljs.core.get.call(null,map__36273__$1,new cljs.core.Keyword(null,"current-url-length","current-url-length",380404083));
return (current_url_length - match_length);
}),cljs.core.keep.call(null,(function (p1__36271_SHARP_){
return figwheel.client.file_reloading.matches_file_QMARK_.call(null,f_data,p1__36271_SHARP_);
}),figwheel.client.file_reloading.current_links.call(null))));
if(cljs.core.truth_(temp__5753__auto__)){
var res = temp__5753__auto__;
return new cljs.core.Keyword(null,"link","link",-1769163468).cljs$core$IFn$_invoke$arity$1(res);
} else {
return null;
}
});
figwheel.client.file_reloading.clone_link = (function figwheel$client$file_reloading$clone_link(link,url){
var clone = document.createElement("link");
clone.rel = "stylesheet";

clone.media = link.media;

clone.disabled = link.disabled;

clone.href = figwheel.client.file_reloading.add_cache_buster.call(null,url);

return clone;
});
figwheel.client.file_reloading.create_link = (function figwheel$client$file_reloading$create_link(url){
var link = document.createElement("link");
link.rel = "stylesheet";

link.href = figwheel.client.file_reloading.add_cache_buster.call(null,url);

return link;
});
figwheel.client.file_reloading.distinctify = (function figwheel$client$file_reloading$distinctify(key,seqq){
return cljs.core.vals.call(null,cljs.core.reduce.call(null,(function (p1__36275_SHARP_,p2__36276_SHARP_){
return cljs.core.assoc.call(null,p1__36275_SHARP_,cljs.core.get.call(null,p2__36276_SHARP_,key),p2__36276_SHARP_);
}),cljs.core.PersistentArrayMap.EMPTY,seqq));
});
figwheel.client.file_reloading.add_link_to_document = (function figwheel$client$file_reloading$add_link_to_document(orig_link,klone,finished_fn){
var parent = orig_link.parentNode;
if(cljs.core._EQ_.call(null,orig_link,parent.lastChild)){
parent.appendChild(klone);
} else {
parent.insertBefore(klone,orig_link.nextSibling);
}

return setTimeout(((function (parent){
return (function (){
parent.removeChild(orig_link);

return finished_fn.call(null);
});})(parent))
,(300));
});
if((typeof figwheel !== 'undefined') && (typeof figwheel.client !== 'undefined') && (typeof figwheel.client.file_reloading !== 'undefined') && (typeof figwheel.client.file_reloading.reload_css_deferred_chain !== 'undefined')){
} else {
figwheel.client.file_reloading.reload_css_deferred_chain = cljs.core.atom.call(null,goog.async.Deferred.succeed());
}
figwheel.client.file_reloading.reload_css_file = (function figwheel$client$file_reloading$reload_css_file(f_data,fin){
var temp__5751__auto__ = figwheel.client.file_reloading.get_correct_link.call(null,f_data);
if(cljs.core.truth_(temp__5751__auto__)){
var link = temp__5751__auto__;
return figwheel.client.file_reloading.add_link_to_document.call(null,link,figwheel.client.file_reloading.clone_link.call(null,link,link.href),((function (link,temp__5751__auto__){
return (function (){
return fin.call(null,cljs.core.assoc.call(null,f_data,new cljs.core.Keyword(null,"loaded","loaded",-1246482293),true));
});})(link,temp__5751__auto__))
);
} else {
return fin.call(null,f_data);
}
});
figwheel.client.file_reloading.reload_css_files_STAR_ = (function figwheel$client$file_reloading$reload_css_files_STAR_(deferred,f_datas,on_cssload){
return figwheel.client.utils.liftContD.call(null,figwheel.client.utils.mapConcatD.call(null,deferred,figwheel.client.file_reloading.reload_css_file,f_datas),(function (f_datas_SINGLEQUOTE_,fin){
var loaded_f_datas_36277 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded","loaded",-1246482293),f_datas_SINGLEQUOTE_);
figwheel.client.file_reloading.on_cssload_custom_event.call(null,loaded_f_datas_36277);

if(cljs.core.fn_QMARK_.call(null,on_cssload)){
on_cssload.call(null,loaded_f_datas_36277);
} else {
}

return fin.call(null);
}));
});
figwheel.client.file_reloading.reload_css_files = (function figwheel$client$file_reloading$reload_css_files(p__36278,p__36279){
var map__36280 = p__36278;
var map__36280__$1 = (((((!((map__36280 == null))))?(((((map__36280.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36280.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36280):map__36280);
var on_cssload = cljs.core.get.call(null,map__36280__$1,new cljs.core.Keyword(null,"on-cssload","on-cssload",1825432318));
var map__36281 = p__36279;
var map__36281__$1 = (((((!((map__36281 == null))))?(((((map__36281.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36281.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36281):map__36281);
var files_msg = map__36281__$1;
var files = cljs.core.get.call(null,map__36281__$1,new cljs.core.Keyword(null,"files","files",-472457450));
if(figwheel.client.utils.html_env_QMARK_.call(null)){
var temp__5753__auto__ = cljs.core.not_empty.call(null,figwheel.client.file_reloading.distinctify.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),files));
if(cljs.core.truth_(temp__5753__auto__)){
var f_datas = temp__5753__auto__;
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.reload_css_deferred_chain,figwheel.client.file_reloading.reload_css_files_STAR_,f_datas,on_cssload);
} else {
return null;
}
} else {
return null;
}
});

//# sourceMappingURL=file_reloading.js.map?rel=1642872179733
