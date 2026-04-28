<template>
  <div class="container">
    <div class="header">
      <h1 class="header-title">🍅 番茄计时器</h1>
      <div class="tab-container">
        <div 
          :class="['tab-item', { active: activeTab === 'timer' }]"
          @click="activeTab = 'timer'"
        >
          计时器
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 'tasks' }]"
          @click="activeTab = 'tasks'"
        >
          任务管理
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 'statistics' }]"
          @click="activeTab = 'statistics'"
        >
          统计面板
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 'settings' }]"
          @click="activeTab = 'settings'"
        >
          设置
        </div>
      </div>
    </div>

    <transition name="fade" mode="out-in">
      <div v-if="activeTab === 'timer'" key="timer">
        <TimerComponent 
          :work-duration="workDuration"
          :break-duration="breakDuration"
          :selected-task="selectedTask"
          @task-completed="onTaskCompleted"
          @update-document-title="updateDocumentTitle"
        />
      </div>

      <div v-else-if="activeTab === 'tasks'" key="tasks">
        <TaskComponent 
          @select-task="onSelectTask"
          :selected-task-id="selectedTask?.id"
        />
      </div>

      <div v-else-if="activeTab === 'statistics'" key="statistics">
        <StatisticsComponent />
      </div>

      <div v-else-if="activeTab === 'settings'" key="settings">
        <SettingsComponent 
          :work-duration="workDuration"
          :break-duration="breakDuration"
          @update-work-duration="workDuration = $event"
          @update-break-duration="breakDuration = $event"
        />
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import TimerComponent from './components/TimerComponent.vue'
import TaskComponent from './components/TaskComponent.vue'
import StatisticsComponent from './components/StatisticsComponent.vue'
import SettingsComponent from './components/SettingsComponent.vue'
import { requestNotificationPermission, playNotificationSound } from './utils/notification.js'

const activeTab = ref('timer')
const workDuration = ref(parseInt(localStorage.getItem('workDuration')) || 25)
const breakDuration = ref(parseInt(localStorage.getItem('breakDuration')) || 5)
const selectedTask = ref(null)

const updateDocumentTitle = (title) => {
  document.title = title
}

const onSelectTask = (task) => {
  selectedTask.value = task
}

const onTaskCompleted = (record) => {
  if (selectedTask.value && selectedTask.value.id === record.taskId) {
    selectedTask.value.completedPomodoros += 1
  }
}

watch(workDuration, (newVal) => {
  localStorage.setItem('workDuration', newVal)
})

watch(breakDuration, (newVal) => {
  localStorage.setItem('breakDuration', newVal)
})

onMounted(() => {
  requestNotificationPermission()
})
</script>
