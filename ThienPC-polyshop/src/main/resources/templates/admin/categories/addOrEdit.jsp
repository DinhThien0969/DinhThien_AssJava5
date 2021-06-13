
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org"
    xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
    layout:decorate="~{admin/dashboardLayout.jsp}">
    
  <head>

</head>
<body>
        <section class="row" layout:fragmen="content">
            <div class="col-6 offset-3 mt-4 ">
                <form th:action="@{/admin/categories/saveOrUpdate}" method="POST" th:object="${category}">
                    <div class="card">
                        <div class="card-header">
                            <h2>Add New Category</h2>
                        </div>
                        <div class="card-body">
                            <div class="form-group">
                                <label for="categoryId">Category ID:</label>
                                
                                <input type="text" class="form-control" th:field="*{categoryId}"
                                    aria-describedby="categoryIdHid" placeholder="ID" readonly>
									 
                            </div>
                            <div class="form-group">
                                <label for="name">Name</label>
                                <input type="text" class="form-control" th:field="*{name}" id="name"
                                    aria-describedby="helpId" placeholder="Category Name">
                                <small th:if="${#fields.hasErrors('name')}" id="nameHId" class="form-text text-muted">BÃ¡ÂºÂ¯t buÃ¡Â»Â™c nhÃ¡ÂºÂ­p trÃƒÂªn 5 kÃƒÂ½ tÃ¡Â»Â±</small>
                            </div>
                        </div>
                        <div class="card-footer text-muted">

                            <button class="btn btn-secondary "type="reset">Reset</button>
                            <a th:href="@{/admin/categories}" class="btn btn-success">Danh sÃƒÂ¡ch sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m</a>
                            <button class="btn btn-primary"><i class="fas fa-save"></i>
                               Save
                            </button>
                        </div>
                    </div>

                </form>
            </div>
        </section>
       
</body>
</html>