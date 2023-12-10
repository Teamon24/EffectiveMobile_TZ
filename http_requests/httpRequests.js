let url = "localhost:8080"
let xhr = new XMLHttpRequest();

let username = "teamon24"
let password = "12345";

xhr.open('POST', `${url}/signup`);
xhr.send(`{"email": "${username}@gmail.com", "username: "${username}", "password": "${password}"}`);

xhr.open('POST', `${url}/signin`);
xhr.send(`{"email": "${username}@gmail.com", "password": "${password}"}`);

// 4. Этот код сработает после того, как мы получим ответ сервера
xhr.onload = function() {
    if (xhr.status != 200) { // анализируем HTTP-статус ответа, если статус не 200, то произошла ошибка
        alert(`Ошибка ${xhr.status}: ${xhr.statusText}`); // Например, 404: Not Found
    } else { // если всё прошло гладко, выводим результат
        alert(`Готово, получили ${xhr.response.length} байт`); // response -- это ответ сервера
    }
};

xhr.onprogress = function(event) {
    if (event.lengthComputable) {
        alert(`Получено ${event.loaded} из ${event.total} байт`);
    } else {
        alert(`Получено ${event.loaded} байт`); // если в ответе нет заголовка Content-Length
    }

};

xhr.onerror = function() {
    alert("Запрос не удался");
};


http("POST", url + "/signup")
Content-Type: application/json

{

}
http("POST", url + "/signin")
http()/signin
Content-Type: application/json

{
    "email": "{{username}}@gmail.com",
    "password": "{{password}}"
}
###