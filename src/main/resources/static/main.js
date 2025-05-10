fetch('/index', {
    method: 'GET',
    headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('jwt')
    }
}).then(response => {
    if (response.status === 401) {
        window.location.href = '/auth.html';
    } else {
        return response.json();
    }
});