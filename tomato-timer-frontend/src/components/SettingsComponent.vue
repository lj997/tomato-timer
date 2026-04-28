<template>
  <div class="card">
    <h3 style="margin-bottom: 20px; font-size: 20px;">⚙️ 计时设置</h3>
    
    <div class="settings-row">
      <div class="settings-label">默认专注时长</div>
      <div class="settings-value">
        <input 
          type="number" 
          v-model.number="localWorkDuration"
          min="1"
          max="120"
        />
        <span>分钟</span>
      </div>
    </div>
    
    <div class="settings-row">
      <div class="settings-label">默认休息时长</div>
      <div class="settings-value">
        <input 
          type="number" 
          v-model.number="localBreakDuration"
          min="1"
          max="30"
        />
        <span>分钟</span>
      </div>
    </div>
    
    <div class="settings-row">
      <div class="settings-label">浏览器通知</div>
      <div class="settings-value">
        <span v-if="notificationPermission === 'granted'" style="color: var(--secondary-color);">
          ✓ 已开启
        </span>
        <span v-else-if="notificationPermission === 'denied'" style="color: var(--primary-color);">
          ✗ 已拒绝
        </span>
        <button 
          v-else
          class="btn btn-sm btn-outline"
          @click="requestPermission"
        >
          开启通知
        </button>
      </div>
    </div>
  </div>

  <div class="card" style="margin-top: 20px;">
    <h3 style="margin-bottom: 20px; font-size: 20px;">🎵 白噪音</h3>
    
    <div v-if="whiteNoises.length === 0" class="empty-state">
      <div class="empty-state-icon">🎧</div>
      <div class="empty-state-text">白噪音功能需要在后台服务器配置音频文件</div>
    </div>
    
    <div v-else class="white-noise-grid">
      <div 
        v-for="noise in whiteNoises" 
        :key="noise.id"
        :class="['white-noise-item', { active: activeNoiseId === noise.id }]"
        @click="toggleNoise(noise.id)"
      >
        <div class="white-noise-icon">{{ noise.icon }}</div>
        <div class="white-noise-name">{{ noise.name }}</div>
      </div>
    </div>
    
    <div v-if="activeNoiseId" class="volume-control">
      <span>🔊</span>
      <input 
        type="range" 
        min="0" 
        max="100" 
        v-model.number="volume"
        @input="updateVolume"
      />
      <span>{{ volume }}%</span>
      <button 
        class="btn btn-sm btn-ghost"
        @click="stopNoise"
      >
        停止
      </button>
    </div>
    
    <div style="margin-top: 20px; padding: 16px; background: var(--bg-color); border-radius: 12px;">
      <p style="font-size: 14px; color: var(--text-secondary);">
        💡 提示：白噪音可以帮助你在专注时屏蔽外界干扰。你可以在计时器运行时选择播放。
        请确保已在后端配置了音频文件。
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  workDuration: {
    type: Number,
    default: 25
  },
  breakDuration: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update-work-duration', 'update-break-duration'])

const localWorkDuration = ref(props.workDuration)
const localBreakDuration = ref(props.breakDuration)
const notificationPermission = ref('default')

const whiteNoises = ref([
  { id: 'rain', name: '雨声', icon: '🌧️' },
  { id: 'cafe', name: '咖啡馆', icon: '☕' },
  { id: 'forest', name: '森林', icon: '🌲' },
  { id: 'waves', name: '海浪', icon: '🌊' },
  { id: 'fire', name: '壁炉', icon: '🔥' },
  { id: 'wind', name: '微风', icon: '🍃' }
])

const activeNoiseId = ref(null)
const volume = ref(50)
let audioContext = null
let oscillator = null
let gainNode = null

const requestPermission = () => {
  if ('Notification' in window) {
    Notification.requestPermission().then(permission => {
      notificationPermission.value = permission
    })
  }
}

const toggleNoise = (id) => {
  if (activeNoiseId.value === id) {
    stopNoise()
  } else {
    playNoise(id)
  }
}

const playNoise = (id) => {
  stopNoise()
  activeNoiseId.value = id
  
  try {
    if (!audioContext) {
      audioContext = new (window.AudioContext || window.webkitAudioContext)()
    }
    
    createBrownNoise(id)
  } catch (e) {
    console.error('播放白噪音失败:', e)
    activeNoiseId.value = null
  }
}

const createBrownNoise = (id) => {
  if (!audioContext) return
  
  gainNode = audioContext.createGain()
  gainNode.gain.value = volume.value / 100 * 0.3
  
  let baseFreq = 100
  let detune = 0
  
  switch (id) {
    case 'rain':
      baseFreq = 80
      detune = 50
      break
    case 'cafe':
      baseFreq = 200
      detune = 100
      break
    case 'forest':
      baseFreq = 120
      detune = 60
      break
    case 'waves':
      baseFreq = 60
      detune = 40
      break
    case 'fire':
      baseFreq = 150
      detune = 80
      break
    case 'wind':
      baseFreq = 90
      detune = 45
      break
  }
  
  const oscillator1 = audioContext.createOscillator()
  oscillator1.type = 'sine'
  oscillator1.frequency.value = baseFreq
  
  const oscillator2 = audioContext.createOscillator()
  oscillator2.type = 'sine'
  oscillator2.frequency.value = baseFreq * 2
  
  const oscillator3 = audioContext.createOscillator()
  oscillator3.type = 'triangle'
  oscillator3.frequency.value = baseFreq / 2
  
  oscillator1.connect(gainNode)
  oscillator2.connect(gainNode)
  oscillator3.connect(gainNode)
  gainNode.connect(audioContext.destination)
  
  oscillator1.start()
  oscillator2.start()
  oscillator3.start()
  
  oscillator = {
    stop: () => {
      oscillator1.stop()
      oscillator2.stop()
      oscillator3.stop()
    }
  }
}

const stopNoise = () => {
  if (oscillator) {
    oscillator.stop()
    oscillator = null
  }
  if (gainNode) {
    gainNode.disconnect()
    gainNode = null
  }
  activeNoiseId.value = null
}

const updateVolume = () => {
  if (gainNode) {
    gainNode.gain.value = volume.value / 100 * 0.3
  }
}

watch(localWorkDuration, (newVal) => {
  emit('update-work-duration', newVal)
})

watch(localBreakDuration, (newVal) => {
  emit('update-break-duration', newVal)
})

watch(() => props.workDuration, (newVal) => {
  localWorkDuration.value = newVal
})

watch(() => props.breakDuration, (newVal) => {
  localBreakDuration.value = newVal
})

onMounted(() => {
  if ('Notification' in window) {
    notificationPermission.value = Notification.permission
  }
})

onUnmounted(() => {
  stopNoise()
  if (audioContext) {
    audioContext.close()
    audioContext = null
  }
})
</script>
