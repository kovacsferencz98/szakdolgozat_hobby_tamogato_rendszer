

const state =  {
    drawer: false
}

const getters = {
 drawer: (state) => {
     return state.drawer
 }
}

const actions = {

}

const mutations = {
    setDrawer: (state, payload) => (state.drawer = payload),
    toggleDrawer: state => (state.drawer = !state.drawer)
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}