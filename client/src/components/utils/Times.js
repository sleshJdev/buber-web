export default {
  format(dateUtcString) {
    const fullDate = new Date(dateUtcString);
    const offset = fullDate.getTimezoneOffset();
    const localOffset = fullDate.getTime() - (offset * 60000);
    const localDate = new Date(localOffset);
    return localDate.toLocaleString();
  },
};