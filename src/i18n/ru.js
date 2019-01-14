export default {
    pages: {
        '/': 'Главная страница',
        '/test': 'Тестовая страница',
        '/config': 'Настройки',
        '/config/edit': 'Настройки',
        '/stat': 'Статистика'
    },
    default: {
        datePattern: 'dd.MM.yyyy',
        isoDatePattern: 'yyyy-MM-dd',
        saveButton: 'Сохранить',
        editButton: 'Изменить',
        deleteButton: 'Удалить',
        cancelButton: 'Отмена',
        confirmDelete: 'Вы действительно хотите удалить?',
        km: 'км',
        days: 'дн.',
        kmPerDay: 'км/дн',
        kmPerTraining: 'км/тр',
        people: 'чел.',
        peoplePerDay: 'чел/дн',
        trainings: 'тр.',
        trainingsPerDay: 'тр/д'
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
    stat: {
        distanceRange: 'Диапазон расстояний',
        dateRange: 'Диапазон дат',
        recount: 'Пересчет',
        topAllRunners: 'Топ бегунов за все время',
        topIntervalRunners: 'Топ бегунов на отрезке',
        daysCount: 'Количество дней бега',
        daysCountAll: 'Всего',
        daysCountInterval: 'Отрезок',
        distance: 'Километраж',
        distancePerDayAvg: 'Средний в день',
        distancePerTrainingAvg: 'Средняя длина одной пробежки',
        distanceMaxOneMan: 'Максимум от одного человека',
        runners: 'Бегуны',
        runnersCountAll: 'Всего отметилось',
        runnersCountInterval: 'Отметилось на отрезке',
        newRunners: 'Новых на отрезке',
        trainings: 'Тренировки',
        trainingCountAll: 'Всего',
        trainingCountPerDayAvgFunction: 'Среднее в день',
        trainingMaxOneMan: 'Максимум от одного человека'
    },
    config: {
        syncPosts: 'Синхронизация постов',
        syncSeconds: 'Интервал запуска синхронизации (с.)',
        groupId: 'ID группы',
        groupShortLink: 'Домен группы',
        commenting: 'Комментирование',
        commentAccessToken: 'Токен для комментирования',
        commentFromGroup: 'Комментирование от имени группы',
        publishStat: 'Авто-публикация статистики',
    },
    sync: {
        title: 'Синхронизация',
        success: 'Синхронизация прошла успешно'
    }
}