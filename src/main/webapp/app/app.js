var app = angular.module('app', ['xeditable', 'ngResource']);

app.controller('TaskListController', function ($resource) {
    var api = $resource('/api/tasks/:taskId');
    var taskList = this;

    var pages;
    var currentPage;
    var filter = 'all';


    taskList.tasks = loadPage(0);

    taskList.loadPage = function (index) {
        taskList.tasks = loadPage(index);
    };

    taskList.nextPage = function () {
        taskList.tasks = loadPage(pages.next);
    };

    taskList.lastPage = function () {
        taskList.tasks = loadPage(pages.last);
    };

    taskList.firstPage = function () {
        taskList.tasks = loadPage(pages.first);
    };

    taskList.prevPage = function () {
        taskList.tasks = loadPage(pages.prev);
    };

    taskList.active = function (index) {
        if (currentPage == index)
            return 'active';
    };

    taskList.addTask = function () {
        var response = api.save({title: taskList.taskTitle, status: false});
        // console.log(response);
        if (response) {
            taskList.tasks.push(response);
            taskList.taskTitle = '';
        }
    };

    taskList.saveTask = function (task, data) {
        angular.extend(task, {title: data.title});
        return api.save({taskId: task.id}, {id: task.id, title: data.title, status: data.status});
    };

    taskList.delete = function (task, index) {
        var response = api.delete({taskId: task.id});
        taskList.tasks.splice(index, 1);
    };

    taskList.setFilter = function (type) {
        filter = type;
        taskList.tasks = loadPage(0);
    };

    taskList.getFilter = function () {
        return filter;
    };

    function loadPage(index) {
        if (typeof index == 'undefined')
            index = currentPage;

        return api.query({status: filter, page: index}, function (data, headersFun) {
            var headerPages = headersFun().link;
            pages = parseHeaderByPages(headerPages);
            currentPage = index;
            taskList.pageList = getPages(pages.last + 1);
        });
    }

    function getPages(count) {
        var arr = [];
        for (var i = 1; i <= count; i++)
            arr.push({id: i})
        return arr;
    }

    function parseHeaderByPages(header) {
        var parts = header.split(',');
        var links = {};
        angular.forEach(parts, function (p) {
            var section = p.split(';');
            if (section.length != 2) {
                throw new Error("section could not be split on ';'");
            }
            var url = section[0].replace(/<(.*)>/, '$1').trim();
            var queryString = {};
            url.replace(
                new RegExp("([^?=&]+)(=([^&]*))?", "g"),
                function ($0, $1, $2, $3) {
                    queryString[$1] = $3;
                }
            );
            var page = queryString['page'];
            if (angular.isString(page)) {
                page = parseInt(page);
            }
            var name = section[1].replace(/rel="(.*)"/, '$1').trim();
            links[name] = page;
        });
        return links;
    }
});
