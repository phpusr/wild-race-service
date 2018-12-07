export default {
    pages: {
        '/': 'Главная страница',
        '/test': 'Тестовая страница',
        '/config': 'Настройки',
        '/config/edit': 'Настройки',
        '/stat': 'Статистика'
    },
    default: {
        saveButton: 'Сохранить',
        editButton: 'Изменить',
        deleteButton: 'Удалить',
        cancelButton: 'Отмена',
        confirmDelete: 'Вы действительно хотите удалить?'
    },
    post: {
        totalSumDistance: 'Всего пробежали (км)',
        numberOfRuns: 'Количество пробежек',
        numberOfPosts: 'Количество постов',

        editDialogTitle: 'Редактирование поста',
        number: '№',
        status: 'Статус',
        statuses: {
            1: 'Пост успешно обработан',
            2: 'Ошибка при сложении',
            3: 'Ошибка в формате записи',
            4: 'Ошибка в стартовой сумме'
        },
        allStatuses: 'Все',
        distance: 'Дистанция',
        sumDistance: 'Сумма дистанций',
        editReason: 'Причина редактирования',
        filter: 'Фильтр',
        manualEditing: 'Ручная правка',
        lastSyncDate: 'Последняя синхронизация',
        noMoreMessages: '',
        noResults: 'Нет данных'
    },
    config: {
        syncPosts: 'Синхронизация постов',
        syncSeconds: 'Интервал запуска синхронизации (с.)',
        groupId: 'ID группы',
        groupShortLink: 'Домен группы',
        commenting: 'Комментирование',
        commentAccessToken: 'Токен для комментирования',
        commentFromGroup: 'Комментирование от имени группы',
        publishStat: 'Авто-публикация статистики'
    }
}